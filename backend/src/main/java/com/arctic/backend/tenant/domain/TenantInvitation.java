package com.arctic.backend.tenant.domain;

import com.arctic.backend.common.audit.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "v1_tenant_invitations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_invitation_token",
                        columnNames = {"token"}
                )
        },
        indexes = {
                @Index(name = "idx_invitation_tenant_email_status", columnList = "tenant_id,email,status"),
                @Index(name = "idx_invitation_expires_at", columnList = "expires_at")
        }
)
@Entity
public class TenantInvitation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "tenant_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Tenant tenant;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_role", nullable = false, length = 30)
    private MembershipRole role;

    @Column(name = "token", nullable = false, length = 120)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InvitationStatus status;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    private TenantInvitation(
            Tenant tenant,
            String email,
            MembershipRole role,
            String token,
            LocalDateTime expiresAt
    ) {
        this.tenant = tenant;
        this.email = email.trim().toLowerCase();
        this.role = role;
        this.token = token;
        this.status = InvitationStatus.PENDING;
        this.expiresAt = expiresAt;
    }

    public static TenantInvitation create(
            Tenant tenant,
            String email,
            MembershipRole role,
            String token,
            LocalDateTime expiresAt
    ) {
        return new TenantInvitation(tenant, email, role, token, expiresAt);
    }

    public boolean isPending() {
        return this.status == InvitationStatus.PENDING;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    public void accept() {
        this.status = InvitationStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = InvitationStatus.CANCELED;
    }

    public void expire() {
        this.status = InvitationStatus.EXPIRED;
    }
}