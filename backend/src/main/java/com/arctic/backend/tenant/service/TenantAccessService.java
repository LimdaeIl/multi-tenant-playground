package com.arctic.backend.tenant.service;

import com.arctic.backend.tenant.domain.MembershipStatus;
import com.arctic.backend.tenant.domain.UserTenantMembership;
import com.arctic.backend.tenant.repository.UserTenantMembershipRepository;
import com.arctic.backend.user.exception.AuthErrorCode;
import com.arctic.backend.user.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TenantAccessService {

    private final UserTenantMembershipRepository membershipRepository;

    @Transactional(readOnly = true)
    public UserTenantMembership getActiveMembership(Long userId, String tenantCode) {
        return membershipRepository.findByUser_IdAndTenant_CodeAndStatus(
                        userId,
                        normalizeTenantCode(tenantCode),
                        MembershipStatus.ACTIVE
                )
                .orElseThrow(() -> new AuthException(
                        AuthErrorCode.TENANT_ACCESS_DENIED,
                        normalizeTenantCode(tenantCode)
                ));
    }

    @Transactional(readOnly = true)
    public void validateActiveMembership(Long userId, String tenantCode) {
        boolean exists = membershipRepository.existsByUser_IdAndTenant_CodeAndStatus(
                userId,
                normalizeTenantCode(tenantCode),
                MembershipStatus.ACTIVE
        );

        if (!exists) {
            throw new AuthException(
                    AuthErrorCode.TENANT_ACCESS_DENIED,
                    normalizeTenantCode(tenantCode)
            );
        }
    }

    private String normalizeTenantCode(String tenantCode) {
        return tenantCode.trim().toLowerCase();
    }
}