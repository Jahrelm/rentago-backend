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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private TenancyRepository tenancyRepository;

    @Override
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

    @Override
    public List<MaintenanceRequest> getAllRequestForTenant(Integer userId) {
        Optional<Maintenance> maintenanceOptional = maintenanceRepository.findByUser_UserId(userId);
        if (maintenanceOptional.isPresent()) {
            return maintenanceOptional.get().getMaintenanceRequest();
        }
        return new ArrayList<>();
    }


    @Override
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
    @Override
    public List<Property> getAllPropertyByLandlord(Integer landlordId){
        Optional<ApplicationUser> landlordOptional = userRepository.findByUserId(landlordId);
        if (!landlordOptional.isPresent()) {
            return new ArrayList<>();
        }
        ApplicationUser landlord = landlordOptional.get();
        List<Property> properties = propertyRepository.findByLandlord(landlord);
        return new ArrayList<>(properties);
    }

    @Override
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

    @Override
    public Property addPropertyForLandlord(Integer landlordId, Property property, MultipartFile[] photos) throws IOException {
        Optional<ApplicationUser> landlordOptional = userRepository.findByUserId(landlordId);
        if (!landlordOptional.isPresent()) {
            throw new RuntimeException("Landlord not found with ID: " + landlordId);
        }
        
        property.setLandlord(landlordOptional.get());

        if (photos != null && photos.length > 0) {
            List<String> photoPaths = new ArrayList<>();
            Path uploadPath = Paths.get(uploadDir, "properties");
            Files.createDirectories(uploadPath);

            for (MultipartFile photo : photos) {
                if (!photo.isEmpty()) {
                    String filename = UUID.randomUUID() + "_" + photo.getOriginalFilename();
                    Path targetLocation = uploadPath.resolve(filename);
                    Files.copy(photo.getInputStream(), targetLocation);
                    photoPaths.add(targetLocation.toString());
                }
            }
            property.setPhotos(String.join(",", photoPaths));
        }

        return propertyRepository.save(property);
    }

    @Override
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