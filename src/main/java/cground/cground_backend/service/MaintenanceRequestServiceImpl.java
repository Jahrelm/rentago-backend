package cground.cground_backend.service;

import cground.cground_backend.model.Maintenance;
import cground.cground_backend.model.MaintenanceRequest;
import cground.cground_backend.repository.MaintenanceRequestRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MaintenanceRequestServiceImpl implements MaintenanceRequestService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final MaintenanceRequestRepository maintenanceRequestRepository;

    public MaintenanceRequestServiceImpl(MaintenanceRequestRepository maintenanceRequestRepository){
        this.maintenanceRequestRepository = maintenanceRequestRepository;
    }

    @Override
    public Iterable<MaintenanceRequest> list(){
        return maintenanceRequestRepository.findAll();
    }

    @Override
    public void createMaintenanceRequest(String title, String description, MaintenanceRequest.Priority priorityLevel, MultipartFile[] photos, Maintenance maintenance) throws IOException {
        MaintenanceRequest maintenanceRequest = new MaintenanceRequest();
        maintenanceRequest.setTitle(title);
        maintenanceRequest.setDescription(description);
        maintenanceRequest.setPriorityLevel(priorityLevel);
        maintenanceRequest.setStatus(MaintenanceRequest.Status.SUBMITTED);
        maintenanceRequest.setCreatedAt(LocalDateTime.now());
        maintenanceRequest.setMaintenance(maintenance);

        if(photos != null && photos.length > 0){
            List<String> photoPaths = savePhotos(photos);
            maintenanceRequest.setPhoto(String.join(",", photoPaths));
        }

        maintenanceRequestRepository.save(maintenanceRequest);
    }

    private List<String> savePhotos(MultipartFile[] photos) throws IOException {
        List<String> photoPaths = new ArrayList<>();
        for (MultipartFile photo : photos) {
            if (!photo.isEmpty()) {
                String filename = UUID.randomUUID() + "_" + photo.getOriginalFilename();
                Path path = Paths.get(uploadDir, filename);
                Files.copy(photo.getInputStream(), path);
                photoPaths.add(path.toString());
            }
        }
        return photoPaths;
    }

    @Override
    public MaintenanceRequest createRequest(Integer userId, String title, String description, MaintenanceRequest.Priority priorityLevel, MultipartFile photo) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public List<MaintenanceRequest> getRequestsByUserId(Integer userId) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public MaintenanceRequest updateRequestStatus(Long requestId, MaintenanceRequest.Status status) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Maintenance request not found with ID: " + requestId));
        
        request.setStatus(status);
        return maintenanceRequestRepository.save(request);
    }

    @Override
    public MaintenanceRequest getRequestById(Long requestId) {
        return maintenanceRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Maintenance request not found with ID: " + requestId));
    }
} 