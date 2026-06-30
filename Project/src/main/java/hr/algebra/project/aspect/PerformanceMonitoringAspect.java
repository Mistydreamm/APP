package hr.algebra.project.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceMonitoringAspect {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitoringAspect.class);

    // Intercepte les méthodes portant l'annotation @MonitorPerformance
    @Around("@annotation(hr.algebra.project.annotation.MonitorPerformance)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        try {
            return joinPoint.proceed(); // Exécute la méthode cible
        } finally {
            long duration = System.currentTimeMillis() - start;
            logger.info("AOP [Performance] - La méthode {} a pris {} ms à s'exécuter.",
                    joinPoint.getSignature().toShortString(),
                    duration);
        }
    }
}