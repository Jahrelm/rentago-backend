package cground.cground_backend.controller;

import cground.cground_backend.model.Document;
import cground.cground_backend.service.DocumentService;
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
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("tenantId") Integer tenantId,
            @RequestParam("landlordId") Integer landlordId,
            @RequestParam("description") String description,
            @RequestParam("userType") String userType) throws IOException {
        if (!"TENANT".equalsIgnoreCase(userType)) {
            return ResponseEntity.badRequest().body("Only tenants can upload documents");
        }
        return ResponseEntity.ok(documentService.uploadDocument(tenantId, landlordId, description, file));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) throws IOException {
        byte[] data = documentService.downloadDocument(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=document_" + id)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<Document>> getTenantDocuments(
            @PathVariable Integer tenantId,
            @RequestParam String userType) {
        if (!"TENANT".equalsIgnoreCase(userType)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(documentService.getDocumentsByTenant(tenantId));
    }

    @GetMapping("/landlord/{landlordId}")
    public ResponseEntity<List<Document>> getLandlordDocuments(
            @PathVariable Integer landlordId,
            @RequestParam String userType) {
        if (!"LANDLORD".equalsIgnoreCase(userType)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(documentService.getDocumentsByLandlord(landlordId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long id,
            @RequestParam String userType) throws IOException {
        if (!"LANDLORD".equalsIgnoreCase(userType)) {
            return ResponseEntity.badRequest().build();
        }
        documentService.deleteDocument(id);
        return ResponseEntity.ok().build();
    }
}
