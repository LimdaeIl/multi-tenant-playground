package com.arctic.backend.tenant.domain;

import com.arctic.backend.common.audit.BaseEntity;
import com.arctic.backend.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "v1_user_tenant_memberships",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_membership_user_tenant",
                        columnNames = {"user_id", "tenant_id"}
                )
        }
)
@Entity
public class UserTenantMembership extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "tenant_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_role", nullable = false, length = 30)
    private MembershipRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_status", nullable = false, length = 30)
    private MembershipStatus status;

    @Column(name = "is_primary", nullable = false)
    private boolean primaryTenant;

    private UserTenantMembership(
            User user,
            Tenant tenant,
            MembershipRole role,
            MembershipStatus status,
            boolean primaryTenant
    ) {
        this.user = user;
        this.tenant = tenant;
        this.role = role;
        this.status = status;
        this.primaryTenant = primaryTenant;
    }

    public static UserTenantMembership create(
            User user,
            Tenant tenant,
            MembershipRole role,
            boolean primaryTenant
    ) {
        return new UserTenantMembership(
                user,
                tenant,
                role,
                MembershipStatus.ACTIVE,
                primaryTenant
        );
    }

    public boolean isActive() {
        return this.status == MembershipStatus.ACTIVE;
    }

    public void changeRole(MembershipRole role) {
        this.role = role;
    }

    public void activate() {
        this.status = MembershipStatus.ACTIVE;
    }

    public void suspend() {
        this.status = MembershipStatus.SUSPENDED;
    }

    public void markPrimary() {
        this.primaryTenant = true;
    }

    public void unmarkPrimary() {
        this.primaryTenant = false;
    }
}