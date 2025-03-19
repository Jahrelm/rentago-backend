package cground.cground_backend.service;

import cground.cground_backend.model.*;
import cground.cground_backend.model.ApplicationUser;
import cground.cground_backend.model.LoginResponseDTO;
import cground.cground_backend.repository.RoleRepository;
import cground.cground_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Service
@Transactional
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmailService emailService;


    public ApplicationUser registerUser(String username, String password, String fullName, String userType, String phoneNumber){
        String encodedPassword = passwordEncoder.encode(password);
        Set<Role> authorities = new HashSet<>();
        
        // Add only the specific role based on userType
        if (userType != null && userType.equalsIgnoreCase("LANDLORD")) {
            Role landlordRole = roleRepository.findByAuthority("LANDLORD")
                .orElseGet(() -> {
                    Role newRole = new Role("LANDLORD");
                    return roleRepository.save(newRole);
                });
            authorities.add(landlordRole);
        } else {
            Role tenantRole = roleRepository.findByAuthority("TENANT")
                .orElseGet(() -> {
                    Role newRole = new Role("TENANT");
                    return roleRepository.save(newRole);
                });
            authorities.add(tenantRole);
        }

        ApplicationUser newUser = new ApplicationUser(null, username, encodedPassword, authorities, fullName, phoneNumber);
        return userRepository.save(newUser);
    }

    public LoginResponseDTO loginUser(String username, String password) {
        ApplicationUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Wrong email or password"));
        logger.info("Username: {} ", username);

        if (!passwordEncoder.matches(password, user.getPassword())){
            logger.info("Password: {} ", password);
            logger.info("User.getPassword: {}", user.getPassword());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Wrong email or password");
        }
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            String token = tokenService.generateJwt(auth);


            return new LoginResponseDTO(user, token);

        } catch (AuthenticationException e) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong email or password", e);

        }

    }

/*
    public String intiatePasswordReset(String username){
        ApplicationUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"User not found"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        String resetLink = "http://localhost:3000/reset-password\n"+ "Your Token is : " + token;

        emailService.sendEmail(user.getUsername(), "Password Reset Request",
                "To reset your password, click the link below:\n" + resetLink);


        return token;

    }*/
    public void resetPassword(String token, String newPassword) {
        ApplicationUser user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

}