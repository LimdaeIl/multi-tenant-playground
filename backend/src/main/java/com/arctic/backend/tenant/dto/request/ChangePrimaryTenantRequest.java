package com.arctic.backend.tenant.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePrimaryTenantRequest(
        @NotBlank(message = "테넌트 코드는 필수입니다.")
        @Size(max = 50, message = "테넌트 코드는 50자 이하여야 합니다.")
        String tenantCode
) {
}