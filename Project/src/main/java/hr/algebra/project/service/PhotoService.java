package hr.algebra.project.service;

import hr.algebra.project.model.AppUser;
import hr.algebra.project.model.PackageType;
import hr.algebra.project.model.Photo;
import hr.algebra.project.repository.PhotoRepository;
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
    
    @Value("${upload.dir}")
    private String uploadDir;

    public PhotoService(PhotoRepository photoRepository, LoggingService loggingService) {
        this.photoRepository = photoRepository;
        this.loggingService = loggingService;
    }

    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload folder!");
        }
    }

    public Photo uploadPhoto(MultipartFile file, String description, String hashtags, AppUser user) throws IOException {
        // Check limits
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        long uploadsToday = photoRepository.countByAuthorAndUploadedAtAfter(user, startOfDay);
        
        int limit = user.getPackageType() == PackageType.FREE ? 5 : 50; // FREE: 5, PRO: 50
        if (uploadsToday >= limit) {
            throw new RuntimeException("Upload limit reached for package " + user.getPackageType());
        }

        String originalFilename = file.getOriginalFilename();
        String filename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path filepath = Paths.get(uploadDir, filename);
        Files.copy(file.getInputStream(), filepath);

        Photo photo = new Photo();
        photo.setFilename(filename);
        photo.setOriginalFilename(originalFilename);
        photo.setDescription(description);
        photo.setHashtags(hashtags);
        photo.setUploadedAt(LocalDateTime.now());
        photo.setAuthor(user);

        Photo saved = photoRepository.save(photo);
        loggingService.logAction(user.getUsername(), "UPLOAD", "Photo uploaded: " + originalFilename);
        return saved;
    }

    public List<Photo> getAllPhotos() {
        return photoRepository.findAll();
    }

    public Photo getPhotoById(Long id) {
        return photoRepository.findById(id).orElse(null);
    }
    
    public Path getPhotoPath(String filename) {
        return Paths.get(uploadDir).resolve(filename);
    }

    public List<Photo> searchPhotos(String filter) {
        List<Photo> byHashtags = photoRepository.findByHashtagsContainingIgnoreCase(filter);
        List<Photo> byAuthor = photoRepository.findByAuthorUsernameContainingIgnoreCase(filter);
        byHashtags.addAll(byAuthor);
        return byHashtags.stream().distinct().collect(Collectors.toList());
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

    public void deletePhoto(Long id, String username, boolean isAdmin) {
         Photo photo = getPhotoById(id);
         if (photo != null) {
             if (isAdmin || photo.getAuthor().getUsername().equals(username)) {
                 try {
                     Files.deleteIfExists(getPhotoPath(photo.getFilename()));
                     photoRepository.delete(photo);
                     loggingService.logAction(username, "DELETE", "Photo deleted: " + photo.getId());
                 } catch (IOException e) {
                     throw new RuntimeException("Failed to delete file");
                 }
             }
         }
    }
}