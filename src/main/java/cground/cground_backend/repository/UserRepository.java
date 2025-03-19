package cground.cground_backend.repository;

import cground.cground_backend.model.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Integer> {
    Optional<ApplicationUser> findByUsername(String Username);
    Optional<ApplicationUser> findByResetToken(String resetToken);

    Optional<ApplicationUser> findByUserId(int userId);
}
