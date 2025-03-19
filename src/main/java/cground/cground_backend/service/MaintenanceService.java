package cground.cground_backend.service;

import cground.cground_backend.model.ApplicationUser;
import cground.cground_backend.model.Maintenance;
import cground.cground_backend.model.MaintenanceRequest;
import cground.cground_backend.model.Property;
import cground.cground_backend.model.Tenancy;
import cground.cground_backend.repository.MaintenanceRepository;
import cground.cground_backend.repository.PropertyRepository;
import cground.cground_backend.repository.TenancyRepository;
import cground.cground_backend.repository.UserRepository;
import com.sun.tools.javac.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MaintenanceService {

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private TenancyRepository tenancyRepository;

    public Maintenance createMaintenanceForUser(Integer userId) {
        Optional<Maintenance> existingMaintenance = maintenanceRepository.findByUser_UserId(userId);
        if (existingMaintenance.isPresent()) {
            return existingMaintenance.get();
        }

        Optional<ApplicationUser> userOptional = userRepository.findByUserId(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        Maintenance maintenance = new Maintenance();
        maintenance.setUser(userOptional.get());
        return maintenanceRepository.save(maintenance);
    }

    public List<MaintenanceRequest> getAllRequestForTenant(Integer userId){
        Optional<Maintenance> maintenanceOptional = maintenanceRepository.findByUser_UserId(userId);
        if (maintenanceOptional.isPresent()){
            return maintenanceOptional.get().getMaintenanceRequest();
        }
        return new ArrayList<>();

    }

    public List<MaintenanceRequest> getAllRequestsForLandlord(Integer landlordId) {
        Optional<ApplicationUser> landlordOptional = userRepository.findByUserId(landlordId);
        if (!landlordOptional.isPresent()) {
            throw new RuntimeException("Landlord not found with ID: " + landlordId);
        }

        List<ApplicationUser> tenants = getTenantsByLandlord(landlordId);
        List<MaintenanceRequest> allRequests = new ArrayList<>();
        for (ApplicationUser tenant : tenants) {
            Optional<Maintenance> maintenanceOptional = maintenanceRepository.findByUser_UserId(tenant.getUserId());
            if (maintenanceOptional.isPresent()) {
                Maintenance maintenance = maintenanceOptional.get();
                allRequests.addAll(maintenance.getMaintenanceRequest());
            }
        }
        
        return allRequests;
    }

    public List<ApplicationUser> getTenantsByLandlord(Integer landlordId) {
        Optional<ApplicationUser> landlordOptional = userRepository.findByUserId(landlordId);
        if (!landlordOptional.isPresent()) {
            return new ArrayList<>();
        }
        ApplicationUser landlord = landlordOptional.get();
        List<Property> properties = propertyRepository.findByLandlord(landlord);
        Set<ApplicationUser> tenants = new HashSet<>();
        for (Property property : properties) {
            List<Tenancy> tenancies = tenancyRepository.findByPropertyAndActive(property, true);
            for (Tenancy tenancy : tenancies) {
                tenants.add(tenancy.getTenant());
            }
        }
        
        return new ArrayList<>(tenants);
    }

    public Property addPropertyForLandlord(Integer landlordId, Property property) {
        Optional<ApplicationUser> landlordOptional = userRepository.findByUserId(landlordId);
        if (!landlordOptional.isPresent()) {
            throw new RuntimeException("Landlord not found with ID: " + landlordId);
        }
        
        property.setLandlord(landlordOptional.get());
        return propertyRepository.save(property);
    }

    public Tenancy addTenantToProperty(Integer tenantId, Long propertyId, Tenancy tenancy) {
        Optional<ApplicationUser> tenantOptional = userRepository.findByUserId(tenantId);
        if (!tenantOptional.isPresent()) {
            throw new RuntimeException("Tenant not found with ID: " + tenantId);
        }
        
        Optional<Property> propertyOptional = propertyRepository.findById(propertyId);
        if (!propertyOptional.isPresent()) {
            throw new RuntimeException("Property not found with ID: " + propertyId);
        }
        
        tenancy.setTenant(tenantOptional.get());
        tenancy.setProperty(propertyOptional.get());
        tenancy.setStartDate(tenancy.getStartDate());
        tenancy.setEndDate(tenancy.getEndDate());
        tenancy.setActive(tenancy.isActive());
        tenancy.setMonthlyRent(tenancy.getMonthlyRent());

        return tenancyRepository.save(tenancy);
    }
}
