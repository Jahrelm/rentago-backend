package cground.cground_backend.controller;

import cground.cground_backend.model.ApplicationUser;
import cground.cground_backend.model.LoginResponseDTO;
import cground.cground_backend.model.Registration;
import cground.cground_backend.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ApplicationUser registerUser(@RequestBody Registration body){
        return authenticationService.registerUser(
                body.getUsername(), 
                body.getPassword(), 
                body.getFullName(), 
                body.getUserType(),
                body.getPhoneNumber());
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Registration body){
        try {
            LoginResponseDTO response = authenticationService.loginUser(body.getUsername(), body.getPassword());
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}
