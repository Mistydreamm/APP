package hr.algebra.project.service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface StorageService {
    void init();
    String store(MultipartFile file);
    Path load(String file);
    void delete(String filename);
}
