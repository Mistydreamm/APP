package hr.algebra.project.service;

import hr.algebra.project.model.AppUser;
import hr.algebra.project.model.PackageType;
import hr.algebra.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser_Success() {
        String username = "testuser";
        String password = "password";
        PackageType packageType = PackageType.FREE;

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("hashedPassword");
        
        AppUser savedUser = new AppUser();
        savedUser.setUsername(username);
        savedUser.setPassword("hashedPassword");
        savedUser.setPackageType(packageType);
        
        when(userRepository.save(any(AppUser.class))).thenReturn(savedUser);

        AppUser result = userService.registerUser(username, password, packageType);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("hashedPassword", result.getPassword());
        verify(userRepository, times(1)).save(any(AppUser.class));
    }

    @Test
    public void testRegisterUser_DuplicateUsername_ThrowsException() {
        String username = "existinguser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new AppUser()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(username, "password", PackageType.FREE);
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(AppUser.class));
    }
}
