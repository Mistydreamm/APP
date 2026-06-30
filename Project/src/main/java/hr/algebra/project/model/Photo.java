package hr.algebra.project.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String originalFilename;

    private String description;

    private String hashtags; // comma separated or space separated

    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private AppUser author;

    public Photo() {
    }

    public Photo(Long id, String filename, String originalFilename, String description, String hashtags, LocalDateTime uploadedAt, AppUser author) {
        this.id = id;
        this.filename = filename;
        this.originalFilename = originalFilename;
        this.description = description;
        this.hashtags = hashtags;
        this.uploadedAt = uploadedAt;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHashtags() {
        return hashtags;
    }

    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public AppUser getAuthor() {
        return author;
    }

    public void setAuthor(AppUser author) {
        this.author = author;
    }

    public static PhotoBuilder builder() {
        return new PhotoBuilder();
    }

    public static class PhotoBuilder {
        private Long id;
        private String filename;
        private String originalFilename;
        private String description;
        private String hashtags;
        private LocalDateTime uploadedAt;
        private AppUser author;

        public PhotoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PhotoBuilder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public PhotoBuilder originalFilename(String originalFilename) {
            this.originalFilename = originalFilename;
            return this;
        }

        public PhotoBuilder description(String description) {
            this.description = description;
            return this;
        }

        public PhotoBuilder hashtags(String hashtags) {
            this.hashtags = hashtags;
            return this;
        }

        public PhotoBuilder uploadedAt(LocalDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public PhotoBuilder author(AppUser author) {
            this.author = author;
            return this;
        }

        public Photo build() {
            return new Photo(id, filename, originalFilename, description, hashtags, uploadedAt, author);
        }
    }
}