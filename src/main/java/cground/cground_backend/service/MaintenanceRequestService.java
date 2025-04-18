package cground.cground_backend.service;

import cground.cground_backend.model.Maintenance;
import cground.cground_backend.model.MaintenanceRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MaintenanceRequestService {
   
    Iterable<MaintenanceRequest> list();
    void createMaintenanceRequest(String title, String description, MaintenanceRequest.Priority priorityLevel, MultipartFile[] photos, Maintenance maintenance) throws IOException;
    MaintenanceRequest createRequest(Integer userId, String title, String description, MaintenanceRequest.Priority priorityLevel, MultipartFile photo);
    List<MaintenanceRequest> getRequestsByUserId(Integer userId);
    MaintenanceRequest updateRequestStatus(Long requestId, MaintenanceRequest.Status status);
    MaintenanceRequest getRequestById(Long requestId);
} 