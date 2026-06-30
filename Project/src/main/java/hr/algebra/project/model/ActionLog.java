package hr.algebra.project.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "action_logs")
public class ActionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    
    private String action;
    
    private String details;
    
    private LocalDateTime timestamp;

    public ActionLog() {
    }

    public ActionLog(Long id, String username, String action, String details, LocalDateTime timestamp) {
        this.id = id;
        this.username = username;
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public static ActionLogBuilder builder() {
        return new ActionLogBuilder();
    }

    public static class ActionLogBuilder {
        private Long id;
        private String username;
        private String action;
        private String details;
        private LocalDateTime timestamp;

        public ActionLogBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ActionLogBuilder username(String username) {
            this.username = username;
            return this;
        }

        public ActionLogBuilder action(String action) {
            this.action = action;
            return this;
        }

        public ActionLogBuilder details(String details) {
            this.details = details;
            return this;
        }

        public ActionLogBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ActionLog build() {
            return new ActionLog(id, username, action, details, timestamp);
        }
    }
}