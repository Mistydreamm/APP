package hr.algebra.project.repository;

import hr.algebra.project.model.Photo;
import hr.algebra.project.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByAuthor(AppUser author);
    long countByAuthorAndUploadedAtAfter(AppUser author, LocalDateTime since);
    List<Photo> findByHashtagsContainingIgnoreCase(String hashtag);
    List<Photo> findByAuthorUsernameContainingIgnoreCase(String username);
}
