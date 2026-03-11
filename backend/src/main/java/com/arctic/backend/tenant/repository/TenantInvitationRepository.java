package com.arctic.backend.tenant.repository;

import com.arctic.backend.tenant.domain.InvitationStatus;
import com.arctic.backend.tenant.domain.TenantInvitation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantInvitationRepository extends JpaRepository<TenantInvitation, Long> {

    Optional<TenantInvitation> findByToken(String token);

    boolean existsByTenant_IdAndEmailAndStatus(Long tenantId, String email, InvitationStatus status);

    List<TenantInvitation> findAllByTenant_Id(Long tenantId);

    List<TenantInvitation> findAllByTenant_IdAndStatus(Long tenantId, InvitationStatus status);
}