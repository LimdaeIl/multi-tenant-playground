package com.arctic.backend.tenant.service;

import com.arctic.backend.tenant.domain.Tenant;
import com.arctic.backend.tenant.dto.request.CreateTenantRequest;
import com.arctic.backend.tenant.dto.response.TenantResponse;
import com.arctic.backend.tenant.repository.TenantRepository;
import com.arctic.backend.user.exception.AuthErrorCode;
import com.arctic.backend.user.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TenantAdminService {

    private final TenantRepository tenantRepository;

    @Transactional
    public TenantResponse createTenant(CreateTenantRequest request) {
        String normalizedCode = normalizeTenantCode(request.code());

        if (tenantRepository.existsByCode(normalizedCode)) {
            throw new AuthException(AuthErrorCode.TENANT_ALREADY_EXISTS, normalizedCode);
        }

        Tenant tenant = Tenant.create(normalizedCode, request.name());
        Tenant savedTenant = tenantRepository.save(tenant);

        return TenantResponse.from(savedTenant);
    }

    private String normalizeTenantCode(String tenantCode) {
        return tenantCode.trim().toLowerCase();
    }
}