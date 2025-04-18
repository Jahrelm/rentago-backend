package cground.cground_backend.controller;

import cground.cground_backend.model.Property;
import cground.cground_backend.model.Property.PropertyStatus;
import cground.cground_backend.model.Tenancy;
import cground.cground_backend.model.Tenancy.RentStatus;
import cground.cground_backend.model.Tenancy.TenancyStatus;
import cground.cground_backend.model.ApplicationUser;
import cground.cground_backend.service.PropertyService;
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
    private final PropertyService propertyService;
    private final UserService userService;

    public PropertyController(PropertyService propertyService, UserService userService) {
        this.propertyService = propertyService;
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
            
            Property savedProperty = propertyService.addPropertyForLandlord(landlordId, property, photos);
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
            tenancy.setMonthlyRent(monthlyRent);
            
            Tenancy savedTenancy = propertyService.addTenantToProperty(tenant.getUserId(), propertyId, tenancy);
            return ResponseEntity.ok(savedTenancy);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error adding tenant to property: " + e.getMessage());
        }
    }

    @PutMapping("/tenancy/{tenancyId}/status")
    public ResponseEntity<?> updateTenancyStatus(
            @PathVariable Long tenancyId,
            @RequestParam String userType,
            @RequestParam TenancyStatus status) {
        try {
            if (!"LANDLORD".equalsIgnoreCase(userType)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only landlords can update tenancy status");
            }
            
            Tenancy updatedTenancy = propertyService.updateTenancyStatus(tenancyId, status);
            return ResponseEntity.ok(updatedTenancy);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating tenancy status: " + e.getMessage());
        }
    }

    @PutMapping("/tenancy/{tenancyId}/rent-status")
    public ResponseEntity<?> updateRentStatus(
            @PathVariable Long tenancyId,
            @RequestParam String userType,
            @RequestParam RentStatus status) {
        try {
            if (!"LANDLORD".equalsIgnoreCase(userType)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only landlords can update rent status");
            }
            
            Tenancy updatedTenancy = propertyService.updateRentStatus(tenancyId, status);
            return ResponseEntity.ok(updatedTenancy);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating rent status: " + e.getMessage());
        }
    }

    @PutMapping("/{propertyId}/status")
    public ResponseEntity<?> updatePropertyStatus(
            @PathVariable Long propertyId,
            @RequestParam String userType,
            @RequestParam PropertyStatus status) {
        try {
            if (!"LANDLORD".equalsIgnoreCase(userType)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only landlords can update property status");
            }
            
            Property updatedProperty = propertyService.updatePropertyStatus(propertyId, status);
            return ResponseEntity.ok(updatedProperty);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating property status: " + e.getMessage());
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getPropertiesByStatus(
            @PathVariable PropertyStatus status,
            @RequestParam String userType) {
        try {
            if (!"LANDLORD".equalsIgnoreCase(userType)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only landlords can view properties by status");
            }
            
            List<Property> properties = propertyService.getPropertiesByStatus(status);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error retrieving properties: " + e.getMessage());
        }
    }

    @GetMapping("/landlord/{landlordId}/status/{status}")
    public ResponseEntity<?> getPropertiesByLandlordAndStatus(
            @PathVariable Integer landlordId,
            @PathVariable PropertyStatus status,
            @RequestParam String userType) {
        try {
            if (!"LANDLORD".equalsIgnoreCase(userType)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only landlords can view properties by status");
            }
            
            List<Property> properties = propertyService.getPropertiesByLandlordAndStatus(landlordId, status);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error retrieving properties: " + e.getMessage());
        }
    }

    @GetMapping("/tenancy/status/{status}")
    public ResponseEntity<?> getTenanciesByStatus(
            @PathVariable TenancyStatus status,
            @RequestParam String userType) {
        try {
            if (!"LANDLORD".equalsIgnoreCase(userType)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only landlords can view tenancies by status");
            }
            
            List<Tenancy> tenancies = propertyService.getTenanciesByStatus(status);
            return ResponseEntity.ok(tenancies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error retrieving tenancies: " + e.getMessage());
        }
    }

    @GetMapping("/tenancy/rent-status/{status}")
    public ResponseEntity<?> getTenanciesByRentStatus(
            @PathVariable RentStatus status,
            @RequestParam String userType) {
        try {
            if (!"LANDLORD".equalsIgnoreCase(userType)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only landlords can view tenancies by rent status");
            }
            
            List<Tenancy> tenancies = propertyService.getTenanciesByRentStatus(status);
            return ResponseEntity.ok(tenancies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error retrieving tenancies: " + e.getMessage());
        }
    }

    @GetMapping("/search/{landlordId}")
    public ResponseEntity<?> searchProperty(@PathVariable Integer landlordId, @RequestParam String address){
        List<Property> properties = propertyService.searchProperty(landlordId, address);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/{landlordId}")
    public ResponseEntity<?>getAllTenants(@PathVariable Integer landlordId){
        List<ApplicationUser> tenancies = propertyService.getTenantsByLandlord(landlordId);
        return ResponseEntity.ok(tenancies);
    }

    @GetMapping("/landlord/{landlordId}")
    public ResponseEntity<?> getAllPropertiesByLandlord(@PathVariable Integer landlordId){
        List<Property> properties = propertyService.getAllPropertyByLandlord(landlordId);
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
            List<Property> properties = propertyService.getPropertiesByTenant(tenantId);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving rented properties: " + e.getMessage());
        }
    }
}
