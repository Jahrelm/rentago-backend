package cground.cground_backend.repository;

import cground.cground_backend.model.ApplicationUser;
import cground.cground_backend.model.Document;
import cground.cground_backend.model.Document.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByTenant(ApplicationUser tenant);
    List<Document> findByLandlord(ApplicationUser landlord);
    List<Document> findByDocumentType(DocumentType documentType);
}
