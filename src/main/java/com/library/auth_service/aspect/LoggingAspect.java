package com.library.auth_service.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging method execution
 */
@Aspect
@Component
public class LoggingAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    
    /**
     * Log before controller methods
     */
    @Before("execution(* com.library.auth_service.controller.*.*(..))")
    public void logBeforeController(JoinPoint joinPoint) {
        logger.info("Executing: {} with args: {}", 
            joinPoint.getSignature().toShortString(), 
            joinPoint.getArgs());
    }
    
    /**
     * Log after successful controller methods
     */
    @AfterReturning(
        pointcut = "execution(* com.library.auth_service.controller.*.*(..))",
        returning = "result")
    public void logAfterController(JoinPoint joinPoint, Object result) {
        logger.info("Completed: {}", joinPoint.getSignature().toShortString());
    }
    
    /**
     * Log after exceptions in controller methods
     */
    @AfterThrowing(
        pointcut = "execution(* com.library.auth_service.controller.*.*(..))",
        throwing = "exception")
    public void logAfterException(JoinPoint joinPoint, Throwable exception) {
        logger.error("Exception in {}: {}", 
            joinPoint.getSignature().toShortString(), 
            exception.getMessage());
    }
}
