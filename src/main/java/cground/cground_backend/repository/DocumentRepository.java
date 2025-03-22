package cground.cground_backend.repository;

import cground.cground_backend.model.ApplicationUser;
import cground.cground_backend.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByTenant(ApplicationUser tenant);
    List<Document> findByLandlord(ApplicationUser landlord);
}
