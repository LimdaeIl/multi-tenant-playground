package com.arctic.backend.tenant.dto.response;

import com.arctic.backend.tenant.domain.TenantInvitation;
import java.time.LocalDateTime;

public record TenantInvitationResponse(
        Long id,
        String tenantCode,
        String email,
        String role,
        String status,
        String token,
        LocalDateTime expiresAt
) {
    public static TenantInvitationResponse from(TenantInvitation invitation) {
        return new TenantInvitationResponse(
                invitation.getId(),
                invitation.getTenant().getCode(),
                invitation.getEmail(),
                invitation.getRole().name(),
                invitation.getStatus().name(),
                invitation.getToken(),
                invitation.getExpiresAt()
        );
    }
}