package cground.cground_backend.controller;

import cground.cground_backend.dto.SignupRequest;
import cground.cground_backend.dto.AuthResponse;
import cground.cground_backend.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody SignupRequest request) {
        logger.info("Received registration request: {}", request);
        try {
            AuthResponse response = authService.register(request);
            logger.info("Registration successful for user: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Registration failed for user: {}", request.getUsername(), e);
            throw e;
        }
    }
} 