package cground.cground_backend.service;

import cground.cground_backend.model.ApplicationUser;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    ApplicationUser FindUserById(int userId);
}

