package com.arctic.backend.tenant.service;

import com.arctic.backend.tenant.domain.MembershipRole;
import com.arctic.backend.tenant.domain.MembershipStatus;
import com.arctic.backend.tenant.domain.Tenant;
import com.arctic.backend.tenant.domain.UserTenantMembership;
import com.arctic.backend.tenant.repository.TenantRepository;
import com.arctic.backend.tenant.repository.UserTenantMembershipRepository;
import com.arctic.backend.user.exception.AuthErrorCode;
import com.arctic.backend.user.exception.AuthException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TenantAccessService {

    private final TenantRepository tenantRepository;
    private final UserTenantMembershipRepository membershipRepository;

    @Transactional(readOnly = true)
    public Tenant getActiveTenant(String tenantCode) {
        String normalizedTenantCode = normalizeTenantCode(tenantCode);

        Tenant tenant = tenantRepository.findByCode(normalizedTenantCode)
                .orElseThrow(() -> new AuthException(
                        AuthErrorCode.TENANT_NOT_FOUND,
                        normalizedTenantCode
                ));

        if (!tenant.isActive()) {
            throw new AuthException(
                    AuthErrorCode.TENANT_INACTIVE,
                    normalizedTenantCode
            );
        }

        return tenant;
    }

    @Transactional(readOnly = true)
    public UserTenantMembership getActiveMembership(Long userId, String tenantCode) {
        Tenant tenant = getActiveTenant(tenantCode);

        return membershipRepository.findByUser_IdAndTenant_CodeAndStatus(
                        userId,
                        tenant.getCode(),
                        MembershipStatus.ACTIVE
                )
                .orElseThrow(() -> new AuthException(
                        AuthErrorCode.TENANT_ACCESS_DENIED,
                        tenant.getCode()
                ));
    }

    @Transactional(readOnly = true)
    public UserTenantMembership getRequiredMembership(
            Long userId,
            String tenantCode,
            MembershipRole... roles
    ) {
        UserTenantMembership membership = getActiveMembership(userId, tenantCode);

        boolean allowed = Arrays.stream(roles)
                .anyMatch(role -> membership.getRole() == role);

        if (!allowed) {
            throw new AuthException(
                    AuthErrorCode.TENANT_ACCESS_DENIED,
                    membership.getTenant().getCode()
            );
        }

        return membership;
    }

    private String normalizeTenantCode(String tenantCode) {
        return tenantCode.trim().toLowerCase();
    }
}