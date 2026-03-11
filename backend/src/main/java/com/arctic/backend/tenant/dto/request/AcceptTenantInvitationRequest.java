package com.arctic.backend.tenant.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AcceptTenantInvitationRequest(
        @NotBlank
        String token
) {
}