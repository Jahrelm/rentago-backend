package cground.cground_backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Maintenance")
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonManagedReference
    private ApplicationUser user;

    @JsonManagedReference
    @OneToMany(mappedBy = "maintenance", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MaintenanceRequest> maintenanceRequest;

    public Maintenance() {
        this.maintenanceRequest = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public List<MaintenanceRequest> getMaintenanceRequest() {
        return maintenanceRequest;
    }

    public void setMaintenanceRequest(List<MaintenanceRequest> maintenanceRequest) {
        this.maintenanceRequest = maintenanceRequest;
    }
}
