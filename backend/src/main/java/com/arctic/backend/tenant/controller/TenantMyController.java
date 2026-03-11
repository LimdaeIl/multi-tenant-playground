package com.arctic.backend.tenant.controller;

import com.arctic.backend.common.response.ApiResponse;
import com.arctic.backend.common.security.CustomUserDetails;
import com.arctic.backend.tenant.dto.request.ChangePrimaryTenantRequest;
import com.arctic.backend.tenant.dto.response.MyTenantResponse;
import com.arctic.backend.tenant.service.TenantMyService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/tenants/my")
@RestController
public class TenantMyController {

    private final TenantMyService tenantMyService;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MyTenantResponse>>> getMyTenants(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        List<MyTenantResponse> response =
                tenantMyService.getMyTenants(principal.getUserId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/primary")
    public ResponseEntity<ApiResponse<MyTenantResponse>> getMyPrimaryTenant(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        MyTenantResponse response =
                tenantMyService.getMyPrimaryTenant(principal.getUserId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PatchMapping("/primary")
    public ResponseEntity<ApiResponse<Void>> changePrimaryTenant(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody @Valid ChangePrimaryTenantRequest request
    ) {
        tenantMyService.changePrimaryTenant(
                principal.getUserId(),
                request.tenantCode()
        );

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
