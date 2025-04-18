package cground.cground_backend.service;

import cground.cground_backend.model.ApplicationUser;
import cground.cground_backend.model.Property;
import cground.cground_backend.model.Property.PropertyStatus;
import cground.cground_backend.model.Tenancy;
import cground.cground_backend.model.Tenancy.RentStatus;
import cground.cground_backend.model.Tenancy.TenancyStatus;
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
import java.util.stream.Collectors;

@Service
public class PropertyServiceImpl implements PropertyService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenancyRepository tenancyRepository;

    @Override
    public Property addPropertyForLandlord(Integer landlordId, Property property, MultipartFile[] photos) throws IOException {
        Optional<ApplicationUser> landlordOptional = userRepository.findByUserId(landlordId);
        if (!landlordOptional.isPresent()) {
            throw new RuntimeException("Landlord not found with ID: " + landlordId);
        }
        
        property.setLandlord(landlordOptional.get());
        property.setStatus(PropertyStatus.AVAILABLE);

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
    public List<Property> getAllPropertyByLandlord(Integer landlordId) {
        Optional<ApplicationUser> landlordOptional = userRepository.findByUserId(landlordId);
        if (!landlordOptional.isPresent()) {
            return new ArrayList<>();
        }
        ApplicationUser landlord = landlordOptional.get();
        List<Property> properties = propertyRepository.findByLandlord(landlord);
        return new ArrayList<>(properties);
    }

    @Override
    public List<Property> getPropertiesByTenant(Integer tenantId) {
        Optional<ApplicationUser> tenantOptional = userRepository.findByUserId(tenantId);
        if (!tenantOptional.isPresent()) {
            return new ArrayList<>();
        }
        ApplicationUser tenant = tenantOptional.get();
        List<Tenancy> tenancies = tenancyRepository.findByTenantAndStatus(tenant, TenancyStatus.ACTIVE);
        return tenancies.stream()
            .map(Tenancy::getProperty)
            .collect(Collectors.toList());
    }

    @Override
    public List<Property> searchProperty(Integer landlordId, String address) {
        Optional<ApplicationUser> landlordOptional = userRepository.findByUserId(landlordId);
        if (!landlordOptional.isPresent()) {
            throw new RuntimeException("Landlord not found with ID: " + landlordId);
        }
        
        List<Property> properties = propertyRepository.findByAddress(address);
        if (properties.isEmpty()) {
            throw new RuntimeException("No properties found with address: " + address);
        }
        
        List<Property> landlordProperties = properties.stream()
            .filter(property -> property.getLandlord().getUserId().equals(landlordId))
            .collect(Collectors.toList());
            
        if (landlordProperties.isEmpty()) {
            throw new RuntimeException("No properties found with address: " + address + " belonging to the specified landlord");
        }
        
        return landlordProperties;
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
            List<Tenancy> tenancies = tenancyRepository.findByPropertyAndStatus(property, TenancyStatus.ACTIVE);
            for (Tenancy tenancy : tenancies) {
                tenants.add(tenancy.getTenant());
            }
        }
        
        return new ArrayList<>(tenants);
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
        
        Property property = propertyOptional.get();
        if (property.getStatus() != PropertyStatus.AVAILABLE) {
            throw new RuntimeException("Property is not available for rent");
        }
        
        tenancy.setTenant(tenantOptional.get());
        tenancy.setProperty(property);
        tenancy.setStartDate(tenancy.getStartDate());
        tenancy.setEndDate(tenancy.getEndDate());
        tenancy.setMonthlyRent(tenancy.getMonthlyRent());
        tenancy.setTenancyStatus(TenancyStatus.PENDING);
        tenancy.setRentStatus(RentStatus.CURRENT);

        property.setStatus(PropertyStatus.RENTED);
        propertyRepository.save(property);

        return tenancyRepository.save(tenancy);
    }

    @Override
    public Tenancy updateTenancyStatus(Long tenancyId, TenancyStatus status) {
        Optional<Tenancy> tenancyOptional = tenancyRepository.findById(tenancyId);
        if (!tenancyOptional.isPresent()) {
            throw new RuntimeException("Tenancy not found with ID: " + tenancyId);
        }
        
        Tenancy tenancy = tenancyOptional.get();
        Property property = tenancy.getProperty();

        if (status == TenancyStatus.TERMINATED) {
            property.setStatus(PropertyStatus.AVAILABLE);
            propertyRepository.save(property);
        } else if (status == TenancyStatus.ACTIVE) {
            property.setStatus(PropertyStatus.RENTED);
            propertyRepository.save(property);
        }

        tenancy.setTenancyStatus(status);
        return tenancyRepository.save(tenancy);
    }

    @Override
    public Tenancy updateRentStatus(Long tenancyId, RentStatus status) {
        Optional<Tenancy> tenancyOptional = tenancyRepository.findById(tenancyId);
        if (!tenancyOptional.isPresent()) {
            throw new RuntimeException("Tenancy not found with ID: " + tenancyId);
        }
        
        Tenancy tenancy = tenancyOptional.get();
        tenancy.setRentStatus(status);
        return tenancyRepository.save(tenancy);
    }

    @Override
    public List<Tenancy> getTenanciesByStatus(TenancyStatus status) {
        return tenancyRepository.findByTenancyStatus(status);
    }

    @Override
    public List<Tenancy> getTenanciesByRentStatus(RentStatus status) {
        return tenancyRepository.findByRentStatus(status);
    }

    @Override
    public Property updatePropertyStatus(Long propertyId, PropertyStatus status) {
        Optional<Property> propertyOptional = propertyRepository.findById(propertyId);
        if (!propertyOptional.isPresent()) {
            throw new RuntimeException("Property not found with ID: " + propertyId);
        }
        
        Property property = propertyOptional.get();
        property.setStatus(status);
        return propertyRepository.save(property);
    }

    @Override
    public List<Property> getPropertiesByStatus(PropertyStatus status) {
        return propertyRepository.findByStatus(status);
    }

    @Override
    public List<Property> getPropertiesByLandlordAndStatus(Integer landlordId, PropertyStatus status) {
        Optional<ApplicationUser> landlordOptional = userRepository.findByUserId(landlordId);
        if (!landlordOptional.isPresent()) {
            throw new RuntimeException("Landlord not found with ID: " + landlordId);
        }
        
        return propertyRepository.findByLandlordAndStatus(landlordOptional.get(), status);
    }
} 