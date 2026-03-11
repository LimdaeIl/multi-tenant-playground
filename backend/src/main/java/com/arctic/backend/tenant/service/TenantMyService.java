package com.arctic.backend.tenant.service;

import com.arctic.backend.tenant.domain.MembershipStatus;
import com.arctic.backend.tenant.domain.UserTenantMembership;
import com.arctic.backend.tenant.dto.response.MyTenantResponse;
import com.arctic.backend.tenant.repository.UserTenantMembershipRepository;
import com.arctic.backend.user.domain.User;
import com.arctic.backend.user.exception.AuthErrorCode;
import com.arctic.backend.user.exception.AuthException;
import com.arctic.backend.user.repository.UserRepository;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TenantMyService {

    private final UserRepository userRepository;
    private final UserTenantMembershipRepository membershipRepository;

    @Transactional(readOnly = true)
    public List<MyTenantResponse> getMyTenants(Long userId) {
        User user = getUser(userId);

        return membershipRepository.findAllByUser_IdAndStatus(userId, MembershipStatus.ACTIVE)
                .stream()
                .sorted(
                        Comparator
                                .comparing((UserTenantMembership m) ->
                                        !m.getTenant().getId().equals(user.getPrimaryTenantId()))
                                .thenComparing(m -> m.getTenant().getName())
                )
                .map(membership -> MyTenantResponse.from(
                        membership,
                        user.getPrimaryTenantId()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public MyTenantResponse getMyPrimaryTenant(Long userId) {
        User user = getUser(userId);

        if (user.getPrimaryTenantId() == null) {
            throw new AuthException(AuthErrorCode.TENANT_PRIMARY_NOT_FOUND);
        }

        UserTenantMembership membership = membershipRepository.findAllByUser_IdAndStatus(
                        userId,
                        MembershipStatus.ACTIVE
                ).stream()
                .filter(m -> m.getTenant().getId().equals(user.getPrimaryTenantId()))
                .findFirst()
                .orElseThrow(() -> new AuthException(AuthErrorCode.TENANT_PRIMARY_NOT_FOUND));

        return MyTenantResponse.from(membership, user.getPrimaryTenantId());
    }

    @Transactional
    public void changePrimaryTenant(Long userId, String tenantCode) {
        User user = getUser(userId);

        UserTenantMembership targetMembership =
                membershipRepository.findByUser_IdAndTenant_CodeAndStatus(
                                userId,
                                normalizeTenantCode(tenantCode),
                                MembershipStatus.ACTIVE
                        )
                        .orElseThrow(() -> new AuthException(
                                AuthErrorCode.TENANT_ACCESS_DENIED,
                                normalizeTenantCode(tenantCode)
                        ));

        if (targetMembership.getTenant().getId().equals(user.getPrimaryTenantId())) {
            return;
        }

        user.changePrimaryTenant(targetMembership.getTenant().getId());
    }

    private User getUser(Long userId) {
        return userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new AuthException(
                        AuthErrorCode.USER_NOT_FOUND_BY_ID,
                        userId
                ));
    }

    private String normalizeTenantCode(String tenantCode) {
        return tenantCode.trim().toLowerCase();
    }
}