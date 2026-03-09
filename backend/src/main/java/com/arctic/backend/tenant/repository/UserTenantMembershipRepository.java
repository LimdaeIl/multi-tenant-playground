package com.arctic.backend.tenant.repository;

import com.arctic.backend.tenant.domain.MembershipStatus;
import com.arctic.backend.tenant.domain.UserTenantMembership;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTenantMembershipRepository extends JpaRepository<UserTenantMembership, Long> {

    List<UserTenantMembership> findAllByUser_Id(Long userId);

    List<UserTenantMembership> findAllByUser_IdAndStatus(Long userId, MembershipStatus status);

    Optional<UserTenantMembership> findByUser_IdAndTenant_Code(Long userId, String tenantCode);

    Optional<UserTenantMembership> findByUser_IdAndTenant_CodeAndStatus(
            Long userId,
            String tenantCode,
            MembershipStatus status
    );

    boolean existsByUser_IdAndTenant_CodeAndStatus(
            Long userId,
            String tenantCode,
            MembershipStatus status
    );
}