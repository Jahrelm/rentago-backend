package cground.cground_backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document implements Serializable {
    public enum DocumentType {
        LEASE_AGREEMENT,
        INVOICE,
        MAINTENANCE_REPORT,
        OTHER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    private ApplicationUser tenant;

    @ManyToOne
    @JoinColumn(name = "landlord_id", nullable = false)
    private ApplicationUser landlord;

    @ManyToOne
    @JoinColumn(name = "uploaded_by_id", nullable = false)
    private ApplicationUser uploadedBy;

    private String name;
    private String description;
    private String filePath;
    private LocalDateTime uploadedAt;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationUser getTenant() {
        return tenant;
    }

    public void setTenant(ApplicationUser tenant) {
        this.tenant = tenant;
    }

    public ApplicationUser getLandlord() {
        return landlord;
    }

    public void setLandlord(ApplicationUser landlord) {
        this.landlord = landlord;
    }

    public ApplicationUser getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(ApplicationUser uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }
}
