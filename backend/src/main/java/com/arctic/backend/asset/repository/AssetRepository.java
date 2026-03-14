package com.arctic.backend.asset.repository;

import com.arctic.backend.asset.domain.Asset;
import com.arctic.backend.asset.domain.AssetStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    boolean existsByTenant_IdAndCode(Long tenantId, String code);

    Optional<Asset> findByIdAndTenant_Id(Long assetId, Long tenantId);

    Optional<Asset> findByTenant_IdAndCode(Long tenantId, String code);

    List<Asset> findAllByTenant_IdAndStatus(Long tenantId, AssetStatus status);

    List<Asset> findAllByTenant_Id(Long tenantId);
}
