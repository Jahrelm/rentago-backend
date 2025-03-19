package cground.cground_backend.repository;

import cground.cground_backend.model.ApplicationUser;
import cground.cground_backend.model.Property;
import cground.cground_backend.model.Tenancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenancyRepository extends JpaRepository<Tenancy, Long> {
    @Query("SELECT t FROM Tenancy t WHERE t.tenant = :tenant")
    List<Tenancy> findByTenant(@Param("tenant") ApplicationUser tenant);
    
    @Query("SELECT t FROM Tenancy t WHERE t.property = :property")
    List<Tenancy> findByProperty(@Param("property") Property property);
    
    @Query("SELECT t FROM Tenancy t WHERE t.property = :property AND t.active = :active")
    List<Tenancy> findByPropertyAndActive(@Param("property") Property property, @Param("active") boolean active);
    
    @Query("SELECT t FROM Tenancy t WHERE t.tenant = :tenant AND t.active = :active")
    List<Tenancy> findByTenantAndActive(@Param("tenant") ApplicationUser tenant, @Param("active") boolean active);
}
