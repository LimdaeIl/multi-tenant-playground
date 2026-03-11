package com.arctic.backend.asset.service;

import com.arctic.backend.asset.domain.Asset;
import com.arctic.backend.asset.domain.AssetStatus;
import com.arctic.backend.asset.exception.AssetErrorCode;
import com.arctic.backend.asset.exception.AssetException;
import com.arctic.backend.asset.repository.AssetRepository;
import com.arctic.backend.tenant.domain.MembershipRole;
import com.arctic.backend.tenant.domain.UserTenantMembership;
import com.arctic.backend.tenant.service.TenantAccessService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AssetService {

    private final AssetRepository assetRepository;
    private final TenantAccessService tenantAccessService;

    @Transactional
    public Asset createAsset(
            Long userId,
            String tenantCode,
            String code,
            String name,
            String externalKey,
            String description
    ) {
        UserTenantMembership membership = tenantAccessService.getRequiredMembership(
                userId,
                tenantCode,
                MembershipRole.OWNER,
                MembershipRole.ADMIN
        );

        String normalizedCode = normalize(code);
        Long tenantId = membership.getTenant().getId();

        if (normalizedCode.isBlank()) {
            throw new AssetException(AssetErrorCode.ASSET_CODE_INVALID);
        }

        if (assetRepository.existsByTenant_IdAndCode(tenantId, normalizedCode)) {
            throw new AssetException(
                    AssetErrorCode.ASSET_CODE_ALREADY_EXISTS,
                    tenantId,
                    normalizedCode
            );
        }

        Asset asset = Asset.create(
                membership.getTenant(),
                normalizedCode,
                name,
                externalKey,
                description
        );

        return assetRepository.save(asset);
    }

    @Transactional(readOnly = true)
    public List<Asset> getAssets(Long userId, String tenantCode) {
        UserTenantMembership membership =
                tenantAccessService.getActiveMembership(userId, tenantCode);

        return assetRepository.findAllByTenant_IdAndStatus(
                membership.getTenant().getId(),
                AssetStatus.ACTIVE
        );
    }

    @Transactional(readOnly = true)
    public Asset getAsset(Long userId, String tenantCode, Long assetId) {
        UserTenantMembership membership =
                tenantAccessService.getActiveMembership(userId, tenantCode);

        return assetRepository.findByIdAndTenant_Id(assetId, membership.getTenant().getId())
                .orElseThrow(() -> new AssetException(
                        AssetErrorCode.ASSET_NOT_FOUND,
                        assetId
                ));
    }

    private String normalize(String code) {
        if (code == null) {
            return "";
        }
        return code.trim().toLowerCase();
    }
}