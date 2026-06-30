package hr.algebra.project.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class ServiceLoggingAspect {
    private static final Logger logger =  LoggerFactory.getLogger(ServiceLoggingAspect.class);

    @Pointcut("execution(* hr.algebra.project.service.*.*(..))")
    public void serviceMethods (){}

    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint){
        logger.info("[Before] - Method called : {} with arguments : {}",
                joinPoint.getSignature().toShortString(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result){
        logger.info("[After] - Fin de : {}. result : {}",
                joinPoint.getSignature().toString(), result);
    }
}
