package com.arctic.backend.tenant.repository;

import com.arctic.backend.tenant.domain.Tenant;
import com.arctic.backend.tenant.domain.TenantStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findByCode(String code);

    boolean existsByCode(String code);

    List<Tenant> findAllByStatus(TenantStatus status);
}