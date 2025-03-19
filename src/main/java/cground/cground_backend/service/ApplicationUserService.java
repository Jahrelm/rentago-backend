package cground.cground_backend.service;

import cground.cground_backend.model.ApplicationUser;

import java.util.Optional;

public interface ApplicationUserService {
    Optional<ApplicationUser> findById(Integer userId);
}
