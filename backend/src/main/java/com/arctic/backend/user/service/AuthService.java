package com.arctic.backend.user.service;

import static com.arctic.backend.user.exception.UserErrorCode.USER_NOT_FOUND;

import com.arctic.backend.common.jwt.JwtErrorCode;
import com.arctic.backend.common.jwt.JwtTokenProvider;
import com.arctic.backend.common.jwt.TokenException;
import com.arctic.backend.tenant.domain.TenantInvitation;
import com.arctic.backend.tenant.domain.UserTenantMembership;
import com.arctic.backend.tenant.repository.UserTenantMembershipRepository;
import com.arctic.backend.tenant.service.TenantInvitationService;
import com.arctic.backend.user.domain.User;
import com.arctic.backend.user.dto.request.SignInRequest;
import com.arctic.backend.user.dto.request.SignupRequest;
import com.arctic.backend.user.dto.request.SignupWithInvitationRequest;
import com.arctic.backend.user.dto.response.SignInResponse;
import com.arctic.backend.user.dto.response.SignupResponse;
import com.arctic.backend.user.dto.response.TokenReissueResponse;
import com.arctic.backend.user.exception.AuthErrorCode;
import com.arctic.backend.user.exception.AuthException;
import com.arctic.backend.user.repository.UserRepository;
import com.arctic.backend.user.repository.UserTokenRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final UserTenantMembershipRepository membershipRepository;
    private final TenantInvitationService tenantInvitationService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        validateEmailDuplication(request.email());

        User user = User.create(
                normalizeEmail(request.email()),
                passwordEncoder.encode(request.password()),
                request.nickname(),
                request.phone()
        );

        userRepository.save(user);
        return SignupResponse.from(user);
    }

    @Transactional
    public SignupResponse signupWithInvitation(SignupWithInvitationRequest request) {
        validateEmailDuplication(request.email());

        TenantInvitation invitation = tenantInvitationService.getValidInvitationForSignup(
                request.inviteToken(),
                request.email()
        );

        User user = User.create(
                normalizeEmail(request.email()),
                passwordEncoder.encode(request.password()),
                request.nickname(),
                request.phone()
        );

        userRepository.save(user);

        UserTenantMembership membership = UserTenantMembership.create(
                user,
                invitation.getTenant(),
                invitation.getRole()
        );

        membershipRepository.save(membership);

        if (!user.hasPrimaryTenant()) {
            user.changePrimaryTenant(invitation.getTenant().getId());
        }

        tenantInvitationService.completeInvitationAcceptance(
                invitation,
                request.inviteToken()
        );

        return SignupResponse.from(user);
    }

    @Transactional
    public SignInResponse signIn(SignInRequest request) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(normalizeEmail(request.email()))
                .orElseThrow(() -> new AuthException(
                        AuthErrorCode.USER_NOT_FOUND_BY_EMAIL,
                        request.email()
                ));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AuthException(AuthErrorCode.USER_PASSWORD_INCORRECT);
        }

        String at = jwtTokenProvider.generateAt(user.getId(), user.getEmail(), user.getRole());
        String rt = jwtTokenProvider.generateRt(user.getId(), user.getEmail(), user.getRole());

        long refreshTtlMillis = jwtTokenProvider.getRtTtlMillis(rt);
        userTokenRepository.saveRefreshToken(user.getId(), rt, refreshTtlMillis);

        List<UserTenantMembership> memberships =
                membershipRepository.findAllByUser_IdAndStatus(
                        user.getId(),
                        com.arctic.backend.tenant.domain.MembershipStatus.ACTIVE
                );

        return SignInResponse.of(user, at, rt, memberships);
    }

    @Transactional
    public TokenReissueResponse reissue(String at, String rt) {
        Long userId = jwtTokenProvider.getUserId(rt);

        if (userTokenRepository.isRtBlacklisted(rt)) {
            throw new TokenException(JwtErrorCode.INVALID_REFRESH_TOKEN);
        }

        String storedRt = userTokenRepository.findRt(userId);
        if (storedRt == null) {
            throw new TokenException(JwtErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        if (!storedRt.equals(rt)) {
            throw new TokenException(JwtErrorCode.INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new AuthException(USER_NOT_FOUND));

        long rtTtlMillis = jwtTokenProvider.getRtTtlMillis(rt);
        if (rtTtlMillis > 0) {
            userTokenRepository.blacklistRt(rt, rtTtlMillis);
        }

        if (at != null && !at.isBlank()) {
            long atTtlMillis = jwtTokenProvider.getAtTtlMillis(at);
            if (atTtlMillis > 0) {
                userTokenRepository.blacklistAt(at, atTtlMillis);
            }
        }

        String newAt = jwtTokenProvider.generateAt(user.getId(), user.getEmail(), user.getRole());
        String newRt = jwtTokenProvider.generateRt(user.getId(), user.getEmail(), user.getRole());
        long newRtTtlMillis = jwtTokenProvider.getRtTtlMillis(newRt);

        userTokenRepository.saveRefreshToken(userId, newRt, newRtTtlMillis);

        return TokenReissueResponse.of(user.getId(), newAt, newRt);
    }

    @Transactional
    public void logout(String at, String rt) {
        Long userIdByRt = jwtTokenProvider.getUserId(rt);
        Long userIdByAt = jwtTokenProvider.getUserId(at);

        if (!userIdByRt.equals(userIdByAt)) {
            throw new TokenException(JwtErrorCode.INVALID_BEARER_TOKEN);
        }

        if (userTokenRepository.isRtBlacklisted(rt)) {
            throw new TokenException(JwtErrorCode.INVALID_REFRESH_TOKEN);
        }

        if (userTokenRepository.isAtBlacklisted(at)) {
            throw new TokenException(JwtErrorCode.INVALID_ACCESS_TOKEN);
        }

        String storedRt = userTokenRepository.findRt(userIdByRt);
        if (storedRt == null) {
            throw new TokenException(JwtErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        if (!storedRt.equals(rt)) {
            throw new TokenException(JwtErrorCode.INVALID_REFRESH_TOKEN);
        }

        long rtTtlMillis = jwtTokenProvider.getRtTtlMillis(rt);
        if (rtTtlMillis > 0) {
            userTokenRepository.blacklistRt(rt, rtTtlMillis);
        }

        long atTtlMillis = jwtTokenProvider.getAtTtlMillis(at);
        if (atTtlMillis > 0) {
            userTokenRepository.blacklistAt(at, atTtlMillis);
        }

        userTokenRepository.deleteRt(userIdByRt);
    }

    private void validateEmailDuplication(String email) {
        if (userRepository.existsByEmailAndDeletedAtIsNull(normalizeEmail(email))) {
            throw new AuthException(AuthErrorCode.EMAIL_EXISTS);
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}