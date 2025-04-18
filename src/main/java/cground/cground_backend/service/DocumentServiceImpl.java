package cground.cground_backend.service;

import cground.cground_backend.model.ApplicationUser;
import cground.cground_backend.model.Document;
import cground.cground_backend.model.Document.DocumentType;
import cground.cground_backend.repository.DocumentRepository;
import cground.cground_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private Path storageLocation;

    public DocumentServiceImpl(DocumentRepository documentRepository, UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() throws IOException {
        this.storageLocation = Paths.get(uploadDir);
        Files.createDirectories(this.storageLocation);
    }

    @Override
    public String uploadDocument(Integer tenantId, Integer landlordId, Integer uploadedById, String description, DocumentType documentType, MultipartFile file) throws IOException {
        ApplicationUser tenant = userRepository.findByUserId(tenantId)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));
        
        ApplicationUser landlord = userRepository.findByUserId(landlordId)
            .orElseThrow(() -> new RuntimeException("Landlord not found"));

        ApplicationUser uploadedBy = userRepository.findByUserId(uploadedById)
            .orElseThrow(() -> new RuntimeException("Uploader not found"));

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path targetLocation = storageLocation.resolve(filename);
        Files.copy(file.getInputStream(), targetLocation);

        Document document = new Document();
        document.setName(file.getOriginalFilename());
        document.setDescription(description);
        document.setFilePath(targetLocation.toString());
        document.setTenant(tenant);
        document.setLandlord(landlord);
        document.setUploadedBy(uploadedBy);
        document.setUploadedAt(LocalDateTime.now());
        document.setDocumentType(documentType);

        documentRepository.save(document);
        return "File uploaded successfully";
    }

    @Override
    public byte[] downloadDocument(Long documentId) throws IOException {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found"));
        Path filePath = Paths.get(document.getFilePath());
        return Files.readAllBytes(filePath);
    }

    @Override
    public List<Document> getDocumentsByTenant(Integer tenantId) {
        ApplicationUser tenant = userRepository.findByUserId(tenantId)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));
        return documentRepository.findByTenant(tenant);
    }

    @Override
    public List<Document> getDocumentsByLandlord(Integer landlordId) {
        ApplicationUser landlord = userRepository.findByUserId(landlordId)
            .orElseThrow(() -> new RuntimeException("Landlord not found"));
        return documentRepository.findByLandlord(landlord);
    }

    @Override
    public List<Document> getDocumentsByType(DocumentType documentType) {
        return documentRepository.findByDocumentType(documentType);
    }

    @Override
    public void deleteDocument(Long documentId) throws IOException {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found"));
        Path filePath = Paths.get(document.getFilePath());
        Files.deleteIfExists(filePath);
        documentRepository.delete(document);
    }
}
