package cground.cground_backend.controller;

import cground.cground_backend.model.Document;
import cground.cground_backend.model.Document.DocumentType;
import cground.cground_backend.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(
            @RequestParam("tenantId") Integer tenantId,
            @RequestParam("landlordId") Integer landlordId,
            @RequestParam("uploadedById") Integer uploadedById,
            @RequestParam("description") String description,
            @RequestParam("documentType") DocumentType documentType,
            @RequestParam("file") MultipartFile file) throws IOException {
        String result = documentService.uploadDocument(tenantId, landlordId, uploadedById, description, documentType, file);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long documentId) throws IOException {
        byte[] fileContent = documentService.downloadDocument(documentId);
        return ResponseEntity.ok(fileContent);
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<Document>> getDocumentsByTenant(@PathVariable Integer tenantId) {
        List<Document> documents = documentService.getDocumentsByTenant(tenantId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/landlord/{landlordId}")
    public ResponseEntity<List<Document>> getDocumentsByLandlord(@PathVariable Integer landlordId) {
        List<Document> documents = documentService.getDocumentsByLandlord(landlordId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/type/{documentType}")
    public ResponseEntity<List<Document>> getDocumentsByType(@PathVariable DocumentType documentType) {
        List<Document> documents = documentService.getDocumentsByType(documentType);
        return ResponseEntity.ok(documents);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long documentId) throws IOException {
        documentService.deleteDocument(documentId);
        return ResponseEntity.ok().build();
    }
}
