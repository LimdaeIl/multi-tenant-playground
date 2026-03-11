package com.arctic.backend.asset.dto.response;

import com.arctic.backend.asset.domain.Asset;

public record AssetResponse(
        Long id,
        String code,
        String name,
        String externalKey,
        String status,
        String description
) {
    public static AssetResponse from(Asset asset) {
        return new AssetResponse(
                asset.getId(),
                asset.getCode(),
                asset.getName(),
                asset.getExternalKey(),
                asset.getStatus().name(),
                asset.getDescription()
        );
    }
}