package hr.algebra.project.service;

import hr.algebra.project.annotation.MonitorPerformance;
import hr.algebra.project.model.AppUser;
import hr.algebra.project.model.PackageType;
import hr.algebra.project.model.Photo;
import hr.algebra.project.repository.PhotoRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final LoggingService loggingService;
    private final StorageService storageService;
    private final MeterRegistry meterRegistry;

    public PhotoService(PhotoRepository photoRepository, LoggingService loggingService, StorageService storageService, MeterRegistry meterRegistry) {
        this.photoRepository = photoRepository;
        this.loggingService = loggingService;
        this.storageService = storageService;
        this.meterRegistry = meterRegistry;
    }

    public void init() {
        storageService.init();
    }

    @MonitorPerformance
    public Photo uploadPhoto(MultipartFile file, String description, String hashtags, AppUser user) throws IOException {
        // Check limits
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        long uploadsToday = photoRepository.countByAuthorAndUploadedAtAfter(user, startOfDay);

        int limit = user.getPackageType() == PackageType.FREE ? 5 : 50; // FREE: 5, PRO: 50
        if (uploadsToday >= limit) {
            throw new RuntimeException("Upload limit reached for package " + user.getPackageType());
        }

        String originalFilename = file.getOriginalFilename();
        String filename = storageService.store(file);

        Photo photo = new Photo();
        photo.setFilename(filename);
        photo.setOriginalFilename(originalFilename);
        photo.setDescription(description);
        photo.setHashtags(hashtags);
        photo.setUploadedAt(LocalDateTime.now());
        photo.setAuthor(user);

        Photo saved = photoRepository.save(photo);
        loggingService.logAction(user.getUsername(), "UPLOAD", "Photo uploaded: " + originalFilename);

        this.meterRegistry.counter("photo.uploads.total", "package_type", user.getPackageType().name())
                .increment();

        return saved;
    }

    public List<Photo> getAllPhotos() {
        return photoRepository.findAll();
    }

    public Photo getPhotoById(Long id) {
        return photoRepository.findById(id).orElse(null);
    }

    public Path getPhotoPath(String filename) {
        return storageService.load(filename);
    }

    @MonitorPerformance
    public List<Photo> searchPhotos(String filter) {
        return java.util.stream.Stream.concat(
                        photoRepository.findByHashtagsContainingIgnoreCase(filter).stream(),
                        photoRepository.findByAuthorUsernameContainingIgnoreCase(filter).stream()
                )
                .distinct()
                .collect(Collectors.toList());
    }

    public void updatePhoto(Long id, String description, String hashtags, String username, boolean isAdmin) {
        Photo photo = getPhotoById(id);
        if (photo != null) {
            if (isAdmin || photo.getAuthor().getUsername().equals(username)) {
                photo.setDescription(description);
                photo.setHashtags(hashtags);
                photoRepository.save(photo);
                loggingService.logAction(username, "EDIT", "Photo edited: " + photo.getId());
            }
        }
    }

    @MonitorPerformance
    public void deletePhoto(Long id, String username, boolean isAdmin) {
        Photo photo = getPhotoById(id);
        if (photo != null) {
            if (isAdmin || photo.getAuthor().getUsername().equals(username)) {

                storageService.delete(photo.getFilename());
                photoRepository.delete(photo);
                loggingService.logAction(username, "DELETE", "Photo deleted: " + photo.getId());
            }
        }
    }

    public java.util.Map<String, Long> getUploadStatsByUser() {
        return getAllPhotos().stream()
                .collect(Collectors.groupingBy(
                        photo -> photo.getAuthor().getUsername(),
                        Collectors.counting()
                ));
    }

    public void incrementDownloadCounter(String status) {
        this.meterRegistry.counter("photo.downloads.total", "status", status)
                .increment();
    }
}