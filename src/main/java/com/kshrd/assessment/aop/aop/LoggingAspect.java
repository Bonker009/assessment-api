package com.kshrd.assessment.aop.aop;

import com.kshrd.assessment.aop.annotation.LogExecution;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("@annotation(com.kshrd.assessment.aop.annotation.LogExecution)")
    public void logExecutionPointcut() {
    }

    @Pointcut("@within(com.kshrd.assessment.aop.annotation.LogExecution)")
    public void logExecutionClassPointcut() {
    }

    @Pointcut("execution(* com.kshrd.assessment.service.serviceImpl..*(..))")
    public void serviceImplPointcut() {
    }

    @Around("logExecutionPointcut() || logExecutionClassPointcut() || serviceImplPointcut()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogExecution logExecution = method.getAnnotation(LogExecution.class);
        
        if (logExecution == null) {
            logExecution = joinPoint.getTarget().getClass().getAnnotation(LogExecution.class);
        }

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();
        String description = logExecution != null && !logExecution.description().isEmpty() 
            ? logExecution.description() 
            : String.format("%s.%s", className, methodName);

        boolean logParameters = logExecution == null || logExecution.logParameters();
        boolean logReturnValue = logExecution == null || logExecution.logReturnValue();

        if (logParameters) {
            Object[] args = joinPoint.getArgs();
            String argsString = args != null ? Arrays.toString(args) : "[]";
            log.info("Executing: {} with parameters: {}", description, argsString);
        } else {
            log.info("Executing: {}", description);
        }

        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable exception = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable throwable) {
            exception = throwable;
            throw throwable;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            
            if (exception != null) {
                log.error("Failed to execute: {} in {}ms with exception: {}", 
                    description, executionTime, exception.getMessage(), exception);
            } else {
                if (logReturnValue) {
                    String resultString = result != null ? result.toString() : "null";
                    if (resultString.length() > 200) {
                        resultString = resultString.substring(0, 200) + "...";
                    }
                    log.info("Completed: {} in {}ms with result: {}", 
                        description, executionTime, resultString);
                } else {
                    log.info("Completed: {} in {}ms", description, executionTime);
                }
            }
        }
    }
}

