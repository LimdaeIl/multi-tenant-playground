package com.arctic.backend.tenant.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TenantStatus {
    ACTIVE("사용중"),
    INACTIVE("비활성"),
    SUSPENDED("중지");

    private final String description;
}
