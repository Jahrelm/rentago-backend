package cground.cground_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String propertyType; // e.g., apartment, house, condo

    @ManyToOne
    @JoinColumn(name = "landlord_id", nullable = false)
    @JsonIgnore
    private ApplicationUser landlord;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tenancy> tenancies = new ArrayList<>();

    public Property() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public ApplicationUser getLandlord() {
        return landlord;
    }

    public void setLandlord(ApplicationUser landlord) {
        this.landlord = landlord;
    }

    public List<Tenancy> getTenancies() {
        return tenancies;
    }

    public void setTenancies(List<Tenancy> tenancies) {
        this.tenancies = tenancies;
    }

    public void addTenancy(Tenancy tenancy) {
        tenancies.add(tenancy);
        tenancy.setProperty(this);
    }

    public void removeTenancy(Tenancy tenancy) {
        tenancies.remove(tenancy);
        tenancy.setProperty(null);
    }
}
