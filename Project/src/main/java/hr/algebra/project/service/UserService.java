package hr.algebra.project.service;

import hr.algebra.project.model.AppUser;
import hr.algebra.project.model.PackageType;
import hr.algebra.project.model.UserRole;
import hr.algebra.project.repository.UserRepository;
import hr.algebra.project.annotation.MonitorPerformance;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoggingService loggingService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, LoggingService loggingService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loggingService = loggingService;
    }

    @MonitorPerformance
    public AppUser registerUser(String username, String password, PackageType packageType) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    throw new RuntimeException("Username already exists");
                });
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.ROLE_USER);
        user.setPackageType(packageType);
        user.setRegisteredAt(LocalDateTime.now());
        AppUser savedUser = userRepository.save(user);
        loggingService.logAction(username, "REGISTER", "User registered with package " + packageType);
        return savedUser;
    }
    
    public AppUser findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}