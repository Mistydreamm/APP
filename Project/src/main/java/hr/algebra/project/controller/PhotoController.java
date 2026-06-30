package hr.algebra.project.controller;

import hr.algebra.project.model.AppUser;
import hr.algebra.project.model.Photo;
import hr.algebra.project.service.LoggingService;
import hr.algebra.project.service.PhotoService;
import hr.algebra.project.service.UserService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

@Controller
public class PhotoController {

    private final PhotoService photoService;
    private final UserService userService;
    private final LoggingService loggingService;

    public PhotoController(PhotoService photoService, UserService userService, LoggingService loggingService) {
        this.photoService = photoService;
        this.userService = userService;
        this.loggingService = loggingService;
        this.photoService.init();
    }

    @GetMapping("/")
    public String index(Model model, @RequestParam(required = false) String search) {
        List<Photo> photos;
        if (search != null && !search.isEmpty()) {
            photos = photoService.searchPhotos(search);
            model.addAttribute("search", search);
        } else {
            photos = photoService.getAllPhotos();
        }
        model.addAttribute("photos", photos);
        return "index";
    }

    @PostMapping("/photos/upload")
    public String uploadPhoto(@RequestParam("file") MultipartFile file,
                              @RequestParam("description") String description,
                              @RequestParam("hashtags") String hashtags,
                              Authentication authentication,
                              Model model) {
        try {
            AppUser user = userService.findByUsername(authentication.getName());
            photoService.uploadPhoto(file, description, hashtags, user);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return index(model, null); // Basic error handling
        }
    }

    @GetMapping("/photos/download/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadPhoto(@PathVariable Long id, Authentication authentication) {
        Photo photo = photoService.getPhotoById(id);
        if (photo == null) return ResponseEntity.notFound().build();

        try {
            Path file = photoService.getPhotoPath(photo.getFilename());
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                String username = authentication != null ? authentication.getName() : "ANONYMOUS";
                loggingService.logAction(username, "DOWNLOAD", "Downloaded photo: " + photo.getOriginalFilename());



                photoService.incrementDownloadCounter("SUCCESS");

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + photo.getOriginalFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/photos/view/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> viewPhoto(@PathVariable String filename) {
        try {
            Path file = photoService.getPhotoPath(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok().body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/photos/edit/{id}")
    public String editPhoto(@PathVariable Long id,
                            @RequestParam String description,
                            @RequestParam String hashtags,
                            Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        photoService.updatePhoto(id, description, hashtags, authentication.getName(), isAdmin);
        return "redirect:/";
    }
    
    @PostMapping("/photos/delete/{id}")
    public String deletePhoto(@PathVariable Long id, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        photoService.deletePhoto(id, authentication.getName(), isAdmin);
        return "redirect:/";
    }

    // TEMPORARY: Test endpoint to trigger a 500 Internal Server Error
    @GetMapping("/trigger-error")
    @ResponseBody
    public String triggerError() {
        throw new RuntimeException("Simulated HTTP 500 Server Error for metrics testing");
    }
}
