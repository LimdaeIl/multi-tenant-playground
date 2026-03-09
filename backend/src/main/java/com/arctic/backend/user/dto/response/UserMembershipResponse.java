package com.arctic.backend.user.dto.response;

import com.arctic.backend.tenant.domain.UserTenantMembership;

public record UserMembershipResponse(
        String tenantCode,
        String tenantName,
        String membershipRole,
        String membershipStatus,
        boolean primary
) {
    public static UserMembershipResponse from(UserTenantMembership membership) {
        return new UserMembershipResponse(
                membership.getTenant().getCode(),
                membership.getTenant().getName(),
                membership.getRole().name(),
                membership.getStatus().name(),
                membership.isPrimaryTenant()
        );
    }
}