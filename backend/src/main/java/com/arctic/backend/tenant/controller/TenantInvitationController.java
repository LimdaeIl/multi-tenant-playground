package com.arctic.backend.tenant.controller;

import com.arctic.backend.common.response.ApiResponse;
import com.arctic.backend.common.security.CustomUserDetails;
import com.arctic.backend.tenant.dto.request.AcceptTenantInvitationRequest;
import com.arctic.backend.tenant.dto.request.CreateTenantInvitationRequest;
import com.arctic.backend.tenant.dto.response.TenantInvitationPreviewResponse;
import com.arctic.backend.tenant.dto.response.TenantInvitationResponse;
import com.arctic.backend.tenant.service.TenantInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/tenant-invitations")
@RestController
public class TenantInvitationController {

    private final TenantInvitationService tenantInvitationService;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<TenantInvitationResponse>> createInvitation(
            @RequestHeader("X-Tenant-Code") String tenantCode,
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody @Valid CreateTenantInvitationRequest request
    ) {
        TenantInvitationResponse response = tenantInvitationService.createInvitation(
                principal.getUserId(),
                tenantCode,
                request
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<Void>> acceptInvitation(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody @Valid AcceptTenantInvitationRequest request
    ) {
        tenantInvitationService.acceptInvitation(principal.getUserId(), request.token());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{token}")
    public ResponseEntity<ApiResponse<TenantInvitationPreviewResponse>> getInvitationPreview(
            @PathVariable String token
    ) {
        TenantInvitationPreviewResponse response =
                tenantInvitationService.getInvitationPreview(token);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PatchMapping("/{invitationId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelInvitation(
            @RequestHeader("X-Tenant-Code") String tenantCode,
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long invitationId
    ) {
        tenantInvitationService.cancelInvitation(
                principal.getUserId(),
                tenantCode,
                invitationId
        );

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}