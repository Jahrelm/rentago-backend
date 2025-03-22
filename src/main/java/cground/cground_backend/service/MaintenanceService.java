package cground.cground_backend.service;

import cground.cground_backend.model.ApplicationUser;
import cground.cground_backend.model.Maintenance;
import cground.cground_backend.model.MaintenanceRequest;
import cground.cground_backend.model.Property;
import cground.cground_backend.model.Tenancy;

import java.util.List;

public interface MaintenanceService {
    Maintenance createMaintenanceForUser(Integer userId);
    List<MaintenanceRequest> getAllRequestForTenant(Integer userId);
    List<MaintenanceRequest> getAllRequestsForLandlord(Integer landlordId);
    List<ApplicationUser> getTenantsByLandlord(Integer landlordId);
    Property addPropertyForLandlord(Integer landlordId, Property property);
    Tenancy addTenantToProperty(Integer tenantId, Long propertyId, Tenancy tenancy);
}

