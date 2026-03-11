package com.arctic.backend.tenant.dto.request;

import com.arctic.backend.tenant.domain.MembershipRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTenantInvitationRequest(
        @NotBlank
        @Email
        String email,

        @NotNull
        MembershipRole role
) {
}