package hr.algebra.project.component;

import hr.algebra.project.service.LoggingService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEvents {

    private final LoggingService loggingService;

    public AuthenticationEvents(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        Authentication auth = event.getAuthentication();
        String username = auth.getName();
        
        // Exclude anonymous user logins
        if (!"anonymousUser".equals(username)) {
            loggingService.logAction(username, "LOGIN", "User logged in successfully");
        }
    }
}
