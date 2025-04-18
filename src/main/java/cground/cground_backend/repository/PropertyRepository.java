package cground.cground_backend.repository;

import cground.cground_backend.model.ApplicationUser;
import cground.cground_backend.model.Property;
import cground.cground_backend.model.Property.PropertyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByLandlord(ApplicationUser landlord);
    
    @Query("SELECT p FROM Property p WHERE p.address = :address")
    List<Property> findByAddress(@Param("address") String address);
    
    @Query("SELECT p FROM Property p WHERE p.status = :status")
    List<Property> findByStatus(@Param("status") PropertyStatus status);
    
    @Query("SELECT p FROM Property p WHERE p.landlord = :landlord AND p.status = :status")
    List<Property> findByLandlordAndStatus(@Param("landlord") ApplicationUser landlord, @Param("status") PropertyStatus status);
}
