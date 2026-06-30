package hr.algebra.project.service;

import hr.algebra.project.model.ActionLog;
import hr.algebra.project.repository.LogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class LoggingService {
    private static final Logger logger = LoggerFactory.getLogger(LoggingService.class);
    private final LogRepository logRepository;

    public LoggingService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void logAction(String username, String action, String details) {
        ActionLog log = new ActionLog();
        log.setUsername(username != null ? username : "ANONYMOUS");
        log.setAction(action);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());
        logRepository.save(log);
        logger.info("User: {}, Action: {}, Details: {}", username, action, details);
    }
}