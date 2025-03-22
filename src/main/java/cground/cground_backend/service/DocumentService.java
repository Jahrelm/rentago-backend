package cground.cground_backend.service;

import cground.cground_backend.model.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocumentService {
    String uploadDocument(Integer tenantId, Integer landlordId, String description, MultipartFile file) throws IOException;
    byte[] downloadDocument(Long documentId) throws IOException;
    List<Document> getDocumentsByTenant(Integer tenantId);
    List<Document> getDocumentsByLandlord(Integer landlordId);
    void deleteDocument(Long documentId) throws IOException;
}
