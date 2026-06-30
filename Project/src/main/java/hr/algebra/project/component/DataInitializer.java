package hr.algebra.project.component;

import hr.algebra.project.model.AppUser;
import hr.algebra.project.model.PackageType;
import hr.algebra.project.model.UserRole;
import hr.algebra.project.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            AppUser admin = new AppUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ROLE_ADMIN);
            admin.setPackageType(PackageType.PRO);
            admin.setRegisteredAt(LocalDateTime.now());
            userRepository.save(admin);
            System.out.println("Default admin user created: admin / admin123");
        }
    }
}