package cground.cground_backend.service;

import cground.cground_backend.model.ApplicationUser;
import cground.cground_backend.model.LoginResponseDTO;

public interface AuthenticationService {
    ApplicationUser registerUser(String username, String password, String fullName, String userType, String phoneNumber);
    LoginResponseDTO loginUser(String username, String password);
    void resetPassword(String token, String newPassword);
}

