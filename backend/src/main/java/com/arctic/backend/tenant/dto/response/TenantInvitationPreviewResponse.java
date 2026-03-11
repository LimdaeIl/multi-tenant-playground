package com.arctic.backend.tenant.dto.response;

import com.arctic.backend.tenant.domain.TenantInvitation;
import java.time.LocalDateTime;

public record TenantInvitationPreviewResponse(
        String tenantName,
        String email,
        String role,
        String status,
        LocalDateTime expiresAt
) {
    public static TenantInvitationPreviewResponse from(TenantInvitation invitation) {
        return new TenantInvitationPreviewResponse(
                invitation.getTenant().getName(),
                invitation.getEmail(),
                invitation.getRole().name(),
                invitation.getStatus().name(),
                invitation.getExpiresAt()
        );
    }
}