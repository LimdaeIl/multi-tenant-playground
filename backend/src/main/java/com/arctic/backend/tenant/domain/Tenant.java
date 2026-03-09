package com.arctic.backend.tenant.domain;

import com.arctic.backend.common.audit.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "v1_tenants")
@Entity
public class Tenant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TenantStatus status;

    private Tenant(String code, String name, TenantStatus status) {
        this.code = code;
        this.name = name;
        this.status = status;
    }

    public static Tenant create(String code, String name) {
        return new Tenant(code.trim().toLowerCase(), name.trim(), TenantStatus.ACTIVE);
    }

    public static Tenant create(String code, String name, TenantStatus status) {
        return new Tenant(code.trim().toLowerCase(), name.trim(), status);
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void activate() {
        this.status = TenantStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = TenantStatus.INACTIVE;
    }

    public void suspend() {
        this.status = TenantStatus.SUSPENDED;
    }

    public boolean isActive() {
        return this.status == TenantStatus.ACTIVE;
    }
}