package cground.cground_backend.repository;

import cground.cground_backend.model.MaintenanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MaintenanceRequestRepository extends CrudRepository<MaintenanceRequest, Long> {

    @Override
    Optional<MaintenanceRequest> findById(Long id);
}
