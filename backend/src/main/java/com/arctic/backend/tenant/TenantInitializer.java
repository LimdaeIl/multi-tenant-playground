package com.arctic.backend.tenant;

import com.arctic.backend.tenant.domain.Tenant;
import com.arctic.backend.tenant.domain.TenantStatus;
import com.arctic.backend.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TenantInitializer implements CommandLineRunner {

    private final TenantRepository tenantRepository;

    @Override
    public void run(String... args) {
        if (!tenantRepository.existsByCode("alpha")) {
            tenantRepository.save(
                    Tenant.create("alpha", "Alpha Corp", TenantStatus.ACTIVE)
            );
        }

        if (!tenantRepository.existsByCode("beta")) {
            tenantRepository.save(
                    Tenant.create("beta", "Beta Corp", TenantStatus.ACTIVE)
            );
        }
    }
}