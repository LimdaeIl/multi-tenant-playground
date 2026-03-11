package com.arctic.backend.asset.controller;

import com.arctic.backend.asset.domain.Asset;
import com.arctic.backend.asset.dto.request.CreateAssetRequest;
import com.arctic.backend.asset.dto.response.AssetResponse;
import com.arctic.backend.asset.service.AssetService;
import com.arctic.backend.common.response.ApiResponse;
import com.arctic.backend.common.security.CustomUserDetails;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/assets")
@RestController
public class AssetController {

    private final AssetService assetService;

    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<AssetResponse>> create(
            @RequestHeader("X-Tenant-Code") String tenantCode,
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody @Valid CreateAssetRequest request
    ) {
        Asset asset = assetService.createAsset(
                principal.getUserId(),
                tenantCode,
                request.code(),
                request.name(),
                request.externalKey(),
                request.description()
        );

        return ResponseEntity.ok(ApiResponse.success(AssetResponse.from(asset)));
    }

    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AssetResponse>>> getAssets(
            @RequestHeader("X-Tenant-Code") String tenantCode,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        List<AssetResponse> response = assetService.getAssets(
                        principal.getUserId(),
                        tenantCode
                ).stream()
                .map(AssetResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    @GetMapping("/{assetId}")
    public ResponseEntity<ApiResponse<AssetResponse>> getAsset(
            @RequestHeader("X-Tenant-Code") String tenantCode,
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long assetId
    ) {
        Asset asset = assetService.getAsset(
                principal.getUserId(),
                tenantCode,
                assetId
        );

        return ResponseEntity.ok(ApiResponse.success(AssetResponse.from(asset)));
    }
}