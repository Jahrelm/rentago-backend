package cground.cground_backend.service;


import cground.cground_backend.model.ApplicationUser;
import cground.cground_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicationUserServiceImpl implements ApplicationUserService{


    @Autowired
    UserRepository userRepository;

    @Override
    public Optional<ApplicationUser> findById(Integer userId){
        return userRepository.findById(userId);
    }
}
