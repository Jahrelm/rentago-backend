package cground.cground_backend.service;

import cground.cground_backend.model.ApplicationUser;
import cground.cground_backend.model.Maintenance;
import cground.cground_backend.model.MaintenanceRequest;
import cground.cground_backend.model.Property;
import cground.cground_backend.model.Tenancy;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MaintenanceService {
    Maintenance createMaintenanceForUser(Integer userId);
    List<MaintenanceRequest> getAllRequestForTenant(Integer userId);
    List<MaintenanceRequest> getAllRequestsForLandlord(Integer landlordId);
    List<ApplicationUser> getTenantsByLandlord(Integer landlordId);
    Property addPropertyForLandlord(Integer landlordId, Property property, MultipartFile[] photos) throws IOException;
    Tenancy addTenantToProperty(Integer tenantId, Long propertyId, Tenancy tenancy);
    List<Property> getAllPropertyByLandlord(Integer landlordId);
}

