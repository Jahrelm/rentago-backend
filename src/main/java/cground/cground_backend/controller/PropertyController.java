package cground.cground_backend.controller;

import cground.cground_backend.model.MaintenanceRequest;
import cground.cground_backend.model.Property;
import cground.cground_backend.model.Tenancy;
import cground.cground_backend.model.ApplicationUser;
import cground.cground_backend.service.MaintenanceService;
import cground.cground_backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin("*")
public class PropertyController {
    private final MaintenanceService maintenanceService;
    private final UserService userService;

    public PropertyController(MaintenanceService maintenanceService, UserService userService) {
        this.maintenanceService = maintenanceService;
        this.userService = userService;
    }

    @PostMapping("/add/{landlordId}")
    public ResponseEntity<?> addProperty(
            @PathVariable Integer landlordId,
            @RequestParam String userType,
            @RequestParam String address,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String zipCode,
            @RequestParam String propertyType,
            @RequestParam(required = false) Integer units,
            @RequestParam(required = false) MultipartFile[] photos) {
        try {
            if (!"LANDLORD".equalsIgnoreCase(userType)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only landlords can add properties");
            }
            
            Property property = new Property();
            property.setAddress(address);
            property.setCity(city);
            property.setState(state);
            property.setZipCode(zipCode);
            property.setPropertyType(propertyType);
            property.setUnits(units);
            
            Property savedProperty = maintenanceService.addPropertyForLandlord(landlordId, property, photos);
            return ResponseEntity.ok(savedProperty);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error adding property: " + e.getMessage());
        }
    }

    @PostMapping("/{propertyId}")
    public ResponseEntity<?> addTenantToProperty(
            @PathVariable Long propertyId,
            @RequestParam String userType,
            @RequestParam String tenantEmail,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "true") boolean active,
            @RequestParam(required = false, defaultValue = "0.0") double monthlyRent) {
        try {
            if (!"LANDLORD".equalsIgnoreCase(userType)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only landlords can add tenants to properties");
            }
            
            ApplicationUser tenant = (ApplicationUser) userService.loadUserByUsername(tenantEmail);
            
            if (!tenant.getAuthorities().stream()
                    .anyMatch(role -> "TENANT".equalsIgnoreCase(role.getAuthority()))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Specified user is not a tenant");
            }
            
            Tenancy tenancy = new Tenancy();
            tenancy.setStartDate(startDate != null ? startDate : LocalDate.now());
            tenancy.setEndDate(endDate);
            tenancy.setActive(active);
            tenancy.setMonthlyRent(monthlyRent);
            
            Tenancy savedTenancy = maintenanceService.addTenantToProperty(tenant.getUserId(), propertyId, tenancy);
            return ResponseEntity.ok(savedTenancy);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error adding tenant to property: " + e.getMessage());
        }
    }

    @GetMapping("/search/{landlordId}")
    public ResponseEntity<?> searchProperty(@PathVariable Integer landlordId, @RequestParam String address){
        List<Property> properties = maintenanceService.searchProperty(landlordId, address);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/maintenance/{landlordId}")
    public ResponseEntity<?> getLandlordRequests(
            @PathVariable Integer landlordId,
            @RequestParam String userType) {
        try {
            if (!"LANDLORD".equalsIgnoreCase(userType)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only landlords can access this endpoint");
            }
            List<MaintenanceRequest> requests = maintenanceService.getAllRequestsForLandlord(landlordId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving maintenance requests: " + e.getMessage());
        }
    }

    @GetMapping("/{landlordId}")
    public ResponseEntity<?>getAllTenants(@PathVariable Integer landlordId){
        List<ApplicationUser> tenancies = maintenanceService.getTenantsByLandlord(landlordId);
        return ResponseEntity.ok(tenancies);
    }

    @GetMapping("/landlord/{landlordId}")
    public ResponseEntity<?> getAllPropertiesByLandlord(@PathVariable Integer landlordId){
        List<Property> properties = maintenanceService.getAllPropertyByLandlord(landlordId);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<?> getTenantRentedProperties(
            @PathVariable Integer tenantId,
            @RequestParam String userType) {
        try {
            if (!"TENANT".equalsIgnoreCase(userType)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only tenants can access this endpoint");
            }
            List<Property> properties = maintenanceService.getPropertiesByTenant(tenantId);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving rented properties: " + e.getMessage());
        }
    }
}
