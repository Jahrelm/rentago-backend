package cground.cground_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "tenancies")
public class Tenancy {

    public enum TenancyStatus {
        PENDING,
        ACTIVE,
        TERMINATED
    }

    public enum RentStatus {
        CURRENT,
        OVERDUE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    private ApplicationUser tenant;

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    @JsonIgnore
    private Property property;

    private LocalDate startDate;
    private LocalDate endDate;
    private double monthlyRent;

    @Enumerated(EnumType.STRING)
    private TenancyStatus tenancyStatus;

    @Enumerated(EnumType.STRING)
    private RentStatus rentStatus;

    public Tenancy() {
        this.tenancyStatus = TenancyStatus.PENDING;
        this.rentStatus = RentStatus.CURRENT;
    }

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

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(double monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public TenancyStatus getTenancyStatus() {
        return tenancyStatus;
    }

    public void setTenancyStatus(TenancyStatus tenancyStatus) {
        this.tenancyStatus = tenancyStatus;
    }

    public RentStatus getRentStatus() {
        return rentStatus;
    }

    public void setRentStatus(RentStatus rentStatus) {
        this.rentStatus = rentStatus;
    }

    public boolean isActive() {
        return this.tenancyStatus == TenancyStatus.ACTIVE;
    }
}
