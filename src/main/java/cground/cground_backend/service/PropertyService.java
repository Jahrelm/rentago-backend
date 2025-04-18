package cground.cground_backend.service;

import cground.cground_backend.model.ApplicationUser;
import cground.cground_backend.model.Property;
import cground.cground_backend.model.Property.PropertyStatus;
import cground.cground_backend.model.Tenancy;
import cground.cground_backend.model.Tenancy.RentStatus;
import cground.cground_backend.model.Tenancy.TenancyStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PropertyService {
    Property addPropertyForLandlord(Integer landlordId, Property property, MultipartFile[] photos) throws IOException;
    List<Property> getAllPropertyByLandlord(Integer landlordId);
    List<Property> getPropertiesByTenant(Integer tenantId);
    List<Property> searchProperty(Integer landlordId, String address);
    List<ApplicationUser> getTenantsByLandlord(Integer landlordId);
    Tenancy addTenantToProperty(Integer tenantId, Long propertyId, Tenancy tenancy);
    Tenancy updateTenancyStatus(Long tenancyId, TenancyStatus status);
    Tenancy updateRentStatus(Long tenancyId, RentStatus status);
    List<Tenancy> getTenanciesByStatus(TenancyStatus status);
    List<Tenancy> getTenanciesByRentStatus(RentStatus status);
    Property updatePropertyStatus(Long propertyId, PropertyStatus status);
    List<Property> getPropertiesByStatus(PropertyStatus status);
    List<Property> getPropertiesByLandlordAndStatus(Integer landlordId, PropertyStatus status);
} 