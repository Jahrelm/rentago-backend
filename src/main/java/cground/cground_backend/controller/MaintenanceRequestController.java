package cground.cground_backend.controller;

import cground.cground_backend.model.Maintenance;
import cground.cground_backend.model.MaintenanceRequest;
import cground.cground_backend.service.MaintenanceRequestService;
import cground.cground_backend.service.MaintenanceService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceRequestController {

    private final MaintenanceRequestService maintenanceRequestService;
    private final MaintenanceService maintenanceService;

    public MaintenanceRequestController(MaintenanceRequestService maintenanceRequestService, 
                                        MaintenanceService maintenanceService){
        this.maintenanceRequestService = maintenanceRequestService;
        this.maintenanceService = maintenanceService;
    }

    @PostMapping("/{userId}/request")
    public ResponseEntity<String> createMaintenanceRequest(
            @PathVariable Integer userId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam MaintenanceRequest.Priority priorityLevel,
            @RequestParam String userType,
            @RequestParam(required = false) MultipartFile[] photos) {
        try {
            if (!"TENANT".equalsIgnoreCase(userType)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only tenants can create maintenance requests");
            }
            
            Maintenance maintenance = maintenanceService.createMaintenanceForUser(userId);
            maintenanceRequestService.createMaintenanceRequest(title, description, priorityLevel, photos, maintenance);
            
            return ResponseEntity.ok("Maintenance request submitted successfully!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating maintenance request: " + e.getMessage());
        }
    }

    @PutMapping("/{requestId}/status")
    public ResponseEntity<?> updateRequestStatus(
            @PathVariable Long requestId,
            @RequestParam MaintenanceRequest.Status status,
            @RequestParam String userType) {
        try {
            if (!"LANDLORD".equalsIgnoreCase(userType)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only landlords can update maintenance request status");
            }
            
            MaintenanceRequest updatedRequest = maintenanceRequestService.updateRequestStatus(requestId, status);
            return ResponseEntity.ok(updatedRequest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating maintenance request status: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}/getTenantRequests")
    public ResponseEntity<?> getTenantRequests(@PathVariable Integer userId){
        List<MaintenanceRequest> request = maintenanceService.getAllRequestForTenant(userId);
        return ResponseEntity.ok(request);
    }
    
    @GetMapping("/landlord/{landlordId}/requests")
    public ResponseEntity<?> getLandlordRequests(@PathVariable Integer landlordId, @RequestParam String userType) {
        try {
            if (!"LANDLORD".equalsIgnoreCase(userType)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only landlords can access this endpoint");
            }
            List<MaintenanceRequest> requests = maintenanceService.getAllRequestsForLandlord(landlordId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving maintenance requests: " + e.getMessage());
        }
    }
}
