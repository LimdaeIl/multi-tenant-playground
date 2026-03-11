package com.arctic.backend.tenant.dto.response;

import com.arctic.backend.tenant.domain.Tenant;

public record TenantResponse(
        Long id,
        String code,
        String name,
        String status
) {
    public static TenantResponse from(Tenant tenant) {
        return new TenantResponse(
                tenant.getId(),
                tenant.getCode(),
                tenant.getName(),
                tenant.getStatus().name()
        );
    }
}