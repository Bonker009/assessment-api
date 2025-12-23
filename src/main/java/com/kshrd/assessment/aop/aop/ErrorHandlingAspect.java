package com.kshrd.assessment.aop.aop;

import com.kshrd.assessment.aop.annotation.LogError;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class ErrorHandlingAspect {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandlingAspect.class);

    @Pointcut("@annotation(com.kshrd.assessment.aop.annotation.LogError)")
    public void logErrorPointcut() {
    }

    @Pointcut("@within(com.kshrd.assessment.aop.annotation.LogError)")
    public void logErrorClassPointcut() {
    }

    @Pointcut("execution(* com.kshrd.assessment.controller..*(..))")
    public void controllerPointcut() {
    }

    @Pointcut("execution(* com.kshrd.assessment.service..*(..))")
    public void servicePointcut() {
    }

    @AfterThrowing(pointcut = "(logErrorPointcut() || logErrorClassPointcut() || controllerPointcut() || servicePointcut())", throwing = "exception")
    public void logError(JoinPoint joinPoint, Throwable exception) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogError logError = method.getAnnotation(LogError.class);
        
        if (logError == null) {
            logError = joinPoint.getTarget().getClass().getAnnotation(LogError.class);
        }

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();
        String description = logError != null && !logError.description().isEmpty() 
            ? logError.description() 
            : String.format("%s.%s", className, methodName);
        
        boolean logStackTrace = logError == null || logError.logStackTrace();

        String errorMessage = String.format("Error in %s: %s", description, exception.getMessage());
        
        if (logStackTrace) {
            log.error(errorMessage, exception);
        } else {
            log.error(errorMessage);
        }

        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            log.error("Method arguments: {}", java.util.Arrays.toString(args));
        }
    }
}

