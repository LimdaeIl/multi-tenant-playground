package com.arctic.backend.tenant.controller;

import com.arctic.backend.common.response.ApiResponse;
import com.arctic.backend.tenant.dto.request.CreateTenantRequest;
import com.arctic.backend.tenant.dto.response.TenantResponse;
import com.arctic.backend.tenant.service.TenantAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/tenants")
@RestController
public class TenantAdminController implements TenantAdminControllerDocs {

    private final TenantAdminService tenantAdminService;

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<TenantResponse>> createTenant(
            @RequestBody @Valid CreateTenantRequest request
    ) {
        TenantResponse response = tenantAdminService.createTenant(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}