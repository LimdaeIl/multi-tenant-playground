package com.arctic.backend.tenant.dto.response;

import com.arctic.backend.tenant.domain.UserTenantMembership;

public record MyTenantResponse(
        Long tenantId,
        String tenantCode,
        String tenantName,
        String tenantStatus,
        String membershipRole,
        String membershipStatus,
        boolean primary
) {
    public static MyTenantResponse from(
            UserTenantMembership membership,
            Long primaryTenantId
    ) {
        return new MyTenantResponse(
                membership.getTenant().getId(),
                membership.getTenant().getCode(),
                membership.getTenant().getName(),
                membership.getTenant().getStatus().name(),
                membership.getRole().name(),
                membership.getStatus().name(),
                membership.getTenant().getId().equals(primaryTenantId)
        );
    }
}