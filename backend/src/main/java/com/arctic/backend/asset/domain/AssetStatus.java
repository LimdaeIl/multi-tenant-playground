package com.arctic.backend.asset.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssetStatus {
    ACTIVE("사용중"),
    INACTIVE("비활성"),
    ARCHIVED("보관");

    private final String description;
}