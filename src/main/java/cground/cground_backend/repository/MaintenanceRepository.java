package cground.cground_backend.repository;

import cground.cground_backend.model.ApplicationUser;
import cground.cground_backend.model.Maintenance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceRepository extends CrudRepository<Maintenance, Long> {
    List<Maintenance> findByUser(ApplicationUser user);
    Optional<Maintenance> findByUser_UserId(Integer userId);
}
