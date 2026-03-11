package com.arctic.backend.asset.domain;

import com.arctic.backend.common.audit.BaseEntity;
import com.arctic.backend.tenant.domain.Tenant;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "v1_assets",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_asset_tenant_code",
                        columnNames = {"tenant_id", "code"}
                )
        },
        indexes = {
                @Index(name = "idx_asset_tenant_status", columnList = "tenant_id,status"),
                @Index(name = "idx_asset_external_key", columnList = "external_key")
        }
)
@Entity
public class Asset extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "tenant_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Tenant tenant;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "external_key", length = 100)
    private String externalKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AssetStatus status;

    @Column(name = "description", length = 500)
    private String description;

    private Asset(Tenant tenant, String code, String name, String externalKey, String description) {
        this.tenant = tenant;
        this.code = code.trim().toLowerCase();
        this.name = name.trim();
        this.externalKey = externalKey == null ? null : externalKey.trim();
        this.description = description == null ? null : description.trim();
        this.status = AssetStatus.ACTIVE;
    }

    public static Asset create(
            Tenant tenant,
            String code,
            String name,
            String externalKey,
            String description
    ) {
        return new Asset(tenant, code, name, externalKey, description);
    }

    public void updateName(String name) {
        this.name = name.trim();
    }

    public void updateDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public void deactivate() {
        this.status = AssetStatus.INACTIVE;
    }

    public void activate() {
        this.status = AssetStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == AssetStatus.ACTIVE;
    }
}