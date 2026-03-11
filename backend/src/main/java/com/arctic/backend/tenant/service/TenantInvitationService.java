package com.arctic.backend.tenant.service;

import com.arctic.backend.tenant.domain.InvitationStatus;
import com.arctic.backend.tenant.domain.MembershipRole;
import com.arctic.backend.tenant.domain.TenantInvitation;
import com.arctic.backend.tenant.domain.UserTenantMembership;
import com.arctic.backend.tenant.dto.request.CreateTenantInvitationRequest;
import com.arctic.backend.tenant.dto.response.TenantInvitationPreviewResponse;
import com.arctic.backend.tenant.dto.response.TenantInvitationResponse;
import com.arctic.backend.tenant.exception.TenantInvitationErrorCode;
import com.arctic.backend.tenant.exception.TenantInvitationException;
import com.arctic.backend.tenant.repository.TenantInvitationRepository;
import com.arctic.backend.tenant.repository.TenantInvitationTokenRepository;
import com.arctic.backend.tenant.repository.UserTenantMembershipRepository;
import com.arctic.backend.user.domain.User;
import com.arctic.backend.user.exception.AuthErrorCode;
import com.arctic.backend.user.exception.AuthException;
import com.arctic.backend.user.repository.UserRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TenantInvitationService {

    private static final long INVITATION_EXPIRE_DAYS = 7L;

    private final TenantInvitationRepository invitationRepository;
    private final TenantInvitationTokenRepository invitationTokenRepository;
    private final UserTenantMembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final TenantAccessService tenantAccessService;

    @Transactional
    public TenantInvitationResponse createInvitation(
            Long actorUserId,
            String tenantCode,
            CreateTenantInvitationRequest request
    ) {
        UserTenantMembership actorMembership = tenantAccessService.getRequiredMembership(
                actorUserId,
                tenantCode,
                MembershipRole.OWNER,
                MembershipRole.ADMIN
        );

        String normalizedEmail = normalizeEmail(request.email());
        Long tenantId = actorMembership.getTenant().getId();

        validatePendingInvitationDuplication(tenantId, normalizedEmail);
        validateAlreadyJoined(normalizedEmail, actorMembership.getTenant().getCode());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusDays(INVITATION_EXPIRE_DAYS);
        String token = generateToken();

        TenantInvitation invitation = TenantInvitation.create(
                actorMembership.getTenant(),
                normalizedEmail,
                request.role(),
                token,
                expiresAt
        );

        TenantInvitation savedInvitation = invitationRepository.save(invitation);

        long ttlMillis = Duration.between(now, expiresAt).toMillis();
        invitationTokenRepository.save(token, savedInvitation.getId(), ttlMillis);

        return TenantInvitationResponse.from(savedInvitation);
    }

    @Transactional
    public void acceptInvitation(Long userId, String token) {
        TenantInvitation invitation = resolveValidInvitation(token);

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new AuthException(
                        AuthErrorCode.USER_NOT_FOUND_BY_ID,
                        userId
                ));

        validateInvitationEmail(invitation, user.getEmail());
        validateMembershipNotExists(user.getId(), invitation.getTenant().getCode(), token);

        UserTenantMembership membership = UserTenantMembership.create(
                user,
                invitation.getTenant(),
                invitation.getRole()
        );

        membershipRepository.save(membership);

        if (!user.hasPrimaryTenant()) {
            user.changePrimaryTenant(invitation.getTenant().getId());
        }

        completeInvitationAcceptance(invitation, token);
    }

    @Transactional(readOnly = true)
    public TenantInvitationPreviewResponse getInvitationPreview(String token) {
        TenantInvitation invitation = resolvePreviewInvitation(token);
        return TenantInvitationPreviewResponse.from(invitation);
    }

    @Transactional(readOnly = true)
    public TenantInvitation getValidInvitationForSignup(String token, String email) {
        TenantInvitation invitation = resolveValidInvitation(token);
        validateInvitationEmail(invitation, email);
        return invitation;
    }

    @Transactional
    public void completeInvitationAcceptance(TenantInvitation invitation, String token) {
        invitation.accept();
        invitationTokenRepository.delete(token);
    }

    @Transactional
    public void cancelInvitation(Long actorUserId, String tenantCode, Long invitationId) {
        UserTenantMembership actorMembership = tenantAccessService.getRequiredMembership(
                actorUserId,
                tenantCode,
                MembershipRole.OWNER,
                MembershipRole.ADMIN
        );

        TenantInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new TenantInvitationException(
                        TenantInvitationErrorCode.INVITATION_NOT_FOUND
                ));

        validateSameTenant(actorMembership, invitation);

        if (!invitation.isPending()) {
            throw new TenantInvitationException(TenantInvitationErrorCode.INVITATION_NOT_PENDING);
        }

        invitation.cancel();
        invitationTokenRepository.delete(invitation.getToken());
    }

    private void validatePendingInvitationDuplication(Long tenantId, String email) {
        if (invitationRepository.existsByTenant_IdAndEmailAndStatus(
                tenantId,
                email,
                InvitationStatus.PENDING
        )) {
            throw new TenantInvitationException(
                    TenantInvitationErrorCode.INVITATION_ALREADY_EXISTS,
                    email
            );
        }
    }

    private void validateAlreadyJoined(String email, String tenantCode) {
        userRepository.findByEmailAndDeletedAtIsNull(email)
                .ifPresent(user -> {
                    boolean exists = membershipRepository.existsByUser_IdAndTenant_Code(
                            user.getId(),
                            tenantCode
                    );

                    if (exists) {
                        throw new AuthException(
                                AuthErrorCode.TENANT_MEMBERSHIP_ALREADY_EXISTS,
                                user.getId(),
                                tenantCode
                        );
                    }
                });
    }

    private TenantInvitation resolveValidInvitation(String token) {
        Long invitationId = invitationTokenRepository.findInvitationId(token);

        if (invitationId == null) {
            throw new TenantInvitationException(
                    TenantInvitationErrorCode.INVITATION_TOKEN_INVALID
            );
        }

        TenantInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new TenantInvitationException(
                        TenantInvitationErrorCode.INVITATION_NOT_FOUND
                ));

        if (!invitation.isPending()) {
            invitationTokenRepository.delete(token);
            throw new TenantInvitationException(TenantInvitationErrorCode.INVITATION_NOT_PENDING);
        }

        if (invitation.isExpired()) {
            invitation.expire();
            invitationTokenRepository.delete(token);
            throw new TenantInvitationException(TenantInvitationErrorCode.INVITATION_EXPIRED);
        }

        return invitation;
    }

    private TenantInvitation resolvePreviewInvitation(String token) {
        Long invitationId = invitationTokenRepository.findInvitationId(token);

        if (invitationId == null) {
            throw new TenantInvitationException(
                    TenantInvitationErrorCode.INVITATION_TOKEN_INVALID
            );
        }

        TenantInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new TenantInvitationException(
                        TenantInvitationErrorCode.INVITATION_NOT_FOUND
                ));

        if (invitation.isExpired() && invitation.getStatus() == InvitationStatus.PENDING) {
            invitation.expire();
            invitationTokenRepository.delete(token);
            throw new TenantInvitationException(TenantInvitationErrorCode.INVITATION_EXPIRED);
        }

        return invitation;
    }

    private void validateInvitationEmail(TenantInvitation invitation, String email) {
        String normalizedEmail = normalizeEmail(email);

        if (!invitation.getEmail().equalsIgnoreCase(normalizedEmail)) {
            throw new TenantInvitationException(
                    TenantInvitationErrorCode.INVITATION_EMAIL_MISMATCH
            );
        }
    }

    private void validateMembershipNotExists(Long userId, String tenantCode, String token) {
        if (membershipRepository.existsByUser_IdAndTenant_Code(userId, tenantCode)) {
            invitationTokenRepository.delete(token);
            throw new AuthException(
                    AuthErrorCode.TENANT_MEMBERSHIP_ALREADY_EXISTS,
                    userId,
                    tenantCode
            );
        }
    }

    private void validateSameTenant(
            UserTenantMembership actorMembership,
            TenantInvitation invitation
    ) {
        if (!invitation.getTenant().getId().equals(actorMembership.getTenant().getId())) {
            throw new AuthException(
                    AuthErrorCode.TENANT_ACCESS_DENIED,
                    actorMembership.getTenant().getCode()
            );
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "");
    }
}