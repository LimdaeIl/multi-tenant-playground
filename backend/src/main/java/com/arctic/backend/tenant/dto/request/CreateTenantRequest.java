package com.arctic.backend.tenant.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTenantRequest(
        @NotBlank
        @Size(max = 50)
        String code,

        @NotBlank
        @Size(max = 100)
        String name
) {
}