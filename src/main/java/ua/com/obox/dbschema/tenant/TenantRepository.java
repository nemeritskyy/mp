package ua.com.obox.dbschema.tenant;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Optional<Tenant> findByTenantId(String tenantId);
}