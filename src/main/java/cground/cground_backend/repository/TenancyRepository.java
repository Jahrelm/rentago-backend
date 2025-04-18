package cground.cground_backend.repository;

import cground.cground_backend.model.ApplicationUser;
import cground.cground_backend.model.Property;
import cground.cground_backend.model.Tenancy;
import cground.cground_backend.model.Tenancy.RentStatus;
import cground.cground_backend.model.Tenancy.TenancyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenancyRepository extends JpaRepository<Tenancy, Long> {
    @Query("SELECT t FROM Tenancy t WHERE t.tenant = :tenant")
    List<Tenancy> findByTenant(@Param("tenant") ApplicationUser tenant);
    
    @Query("SELECT t FROM Tenancy t WHERE t.property = :property")
    List<Tenancy> findByProperty(@Param("property") Property property);
    
    @Query("SELECT t FROM Tenancy t WHERE t.property = :property AND t.tenancyStatus = :status")
    List<Tenancy> findByPropertyAndStatus(@Param("property") Property property, @Param("status") TenancyStatus status);
    
    @Query("SELECT t FROM Tenancy t WHERE t.tenant = :tenant AND t.tenancyStatus = :status")
    List<Tenancy> findByTenantAndStatus(@Param("tenant") ApplicationUser tenant, @Param("status") TenancyStatus status);

    @Query("SELECT t FROM Tenancy t WHERE t.rentStatus = :rentStatus")
    List<Tenancy> findByRentStatus(@Param("rentStatus") RentStatus rentStatus);

    @Query("SELECT t FROM Tenancy t WHERE t.tenancyStatus = :tenancyStatus")
    List<Tenancy> findByTenancyStatus(@Param("tenancyStatus") TenancyStatus tenancyStatus);

    List<Tenancy> findByTenantAndTenancyStatus(ApplicationUser tenant, TenancyStatus status);
    List<Tenancy> findByPropertyAndTenancyStatus(Property property, TenancyStatus status);
    Optional<Tenancy> findById(Long id);
}
