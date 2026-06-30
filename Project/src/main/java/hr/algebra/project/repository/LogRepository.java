package hr.algebra.project.repository;

import hr.algebra.project.model.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<ActionLog, Long> {
}
