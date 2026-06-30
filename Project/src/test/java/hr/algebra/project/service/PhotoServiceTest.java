package hr.algebra.project.service;

import hr.algebra.project.model.AppUser;
import hr.algebra.project.model.PackageType;
import hr.algebra.project.model.Photo;
import hr.algebra.project.repository.PhotoRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PhotoServiceTest {

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private LoggingService loggingService;

    @Mock
    private StorageService storageService;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    @InjectMocks
    private PhotoService photoService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(meterRegistry.counter(anyString(), any(String[].class))).thenReturn(counter);
    }

    @Test
    public void testUploadPhoto_WithinLimit_Success() throws IOException {
        AppUser user = new AppUser();
        user.setUsername("user1");
        user.setPackageType(PackageType.FREE);

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(photoRepository.countByAuthorAndUploadedAtAfter(eq(user), any(LocalDateTime.class))).thenReturn(0L);
        when(storageService.store(file)).thenReturn("uuid_test.jpg");

        Photo savedPhoto = new Photo();
        savedPhoto.setFilename("uuid_test.jpg");
        savedPhoto.setOriginalFilename("test.jpg");
        savedPhoto.setAuthor(user);
        when(photoRepository.save(any(Photo.class))).thenReturn(savedPhoto);

        Photo result = photoService.uploadPhoto(file, "Description", "tags", user);

        assertNotNull(result);
        assertEquals("uuid_test.jpg", result.getFilename());
        verify(storageService, times(1)).store(file);
        verify(photoRepository, times(1)).save(any(Photo.class));
    }

    @Test
    public void testUploadPhoto_ExceededLimit_ThrowsException() throws IOException {
        AppUser user = new AppUser();
        user.setUsername("user1");
        user.setPackageType(PackageType.FREE);

        MultipartFile file = mock(MultipartFile.class);
        when(photoRepository.countByAuthorAndUploadedAtAfter(eq(user), any(LocalDateTime.class))).thenReturn(5L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            photoService.uploadPhoto(file, "Description", "tags", user);
        });

        assertTrue(exception.getMessage().contains("Upload limit reached"));
        verify(storageService, never()).store(any());
        verify(photoRepository, never()).save(any());
    }
}
