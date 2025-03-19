package cground.cground_backend.repository;

import cground.cground_backend.model.ApplicationUser;
import cground.cground_backend.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    @Query("SELECT p FROM Property p WHERE p.landlord = :landlord")
    List<Property> findByLandlord(@Param("landlord") ApplicationUser landlord);
}
