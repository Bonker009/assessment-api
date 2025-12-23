package com.kshrd.assessment.aop.aop;

import com.kshrd.assessment.aop.annotation.LogPerformance;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class PerformanceAspect {

    private static final Logger log = LoggerFactory.getLogger(PerformanceAspect.class);

    @Pointcut("@annotation(com.kshrd.assessment.aop.annotation.LogPerformance)")
    public void logPerformancePointcut() {
    }

    @Pointcut("@within(com.kshrd.assessment.aop.annotation.LogPerformance)")
    public void logPerformanceClassPointcut() {
    }

    @Pointcut("execution(* com.kshrd.assessment.service.serviceImpl..*(..))")
    public void serviceImplPointcut() {
    }

    @Around("logPerformancePointcut() || logPerformanceClassPointcut() || serviceImplPointcut()")
    public Object logPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogPerformance logPerformance = method.getAnnotation(LogPerformance.class);
        
        if (logPerformance == null) {
            logPerformance = joinPoint.getTarget().getClass().getAnnotation(LogPerformance.class);
        }

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();
        String description = logPerformance != null && !logPerformance.description().isEmpty() 
            ? logPerformance.description() 
            : String.format("%s.%s", className, methodName);
        
        long threshold = logPerformance != null ? logPerformance.thresholdMillis() : 1000;

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
            
            if (executionTime >= threshold) {
                if (exception != null) {
                    log.warn("SLOW EXECUTION: {} took {}ms (threshold: {}ms) - FAILED with exception: {}", 
                        description, executionTime, threshold, exception.getMessage());
                } else {
                    log.warn("SLOW EXECUTION: {} took {}ms (threshold: {}ms)", 
                        description, executionTime, threshold);
                }
            } else {
                log.debug("Performance: {} executed in {}ms", description, executionTime);
            }
        }
    }
}

