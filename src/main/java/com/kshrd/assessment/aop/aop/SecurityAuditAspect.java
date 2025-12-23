package com.kshrd.assessment.aop.aop;

import com.kshrd.assessment.aop.annotation.AuditSecurity;
import com.kshrd.assessment.utils.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

@Aspect
@Component
public class SecurityAuditAspect {

    private static final Logger log = LoggerFactory.getLogger(SecurityAuditAspect.class);

    @Pointcut("@annotation(com.kshrd.assessment.aop.annotation.AuditSecurity)")
    public void auditSecurityPointcut() {
    }

    @Pointcut("@within(com.kshrd.assessment.aop.annotation.AuditSecurity)")
    public void auditSecurityClassPointcut() {
    }

    @Pointcut("execution(* com.kshrd.assessment.controller..*(..))")
    public void controllerPointcut() {
    }

    @Before("(auditSecurityPointcut() || auditSecurityClassPointcut() || controllerPointcut())")
    public void auditBefore(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AuditSecurity auditSecurity = method.getAnnotation(AuditSecurity.class);
        
        if (auditSecurity == null) {
            auditSecurity = joinPoint.getTarget().getClass().getAnnotation(AuditSecurity.class);
        }

        if (auditSecurity == null) {
            return;
        }

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();
        String action = auditSecurity.action().isEmpty() 
            ? String.format("%s.%s", className, methodName) 
            : auditSecurity.action();
        String resource = auditSecurity.resource().isEmpty() 
            ? className 
            : auditSecurity.resource();

        UUID userId = null;
        String username = null;
        try {
            userId = SecurityUtils.getCurrentUserId();
            username = SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            log.debug("Could not get current user for audit: {}", e.getMessage());
        }

        String userInfo = userId != null 
            ? String.format("userId=%s, username=%s", userId, username != null ? username : "unknown")
            : "anonymous";

        if (auditSecurity.logParameters()) {
            Object[] args = joinPoint.getArgs();
            String argsString = args != null ? Arrays.toString(args) : "[]";
            log.info("SECURITY AUDIT - BEFORE: action={}, resource={}, user=[{}], parameters={}", 
                action, resource, userInfo, argsString);
        } else {
            log.info("SECURITY AUDIT - BEFORE: action={}, resource={}, user=[{}]", 
                action, resource, userInfo);
        }
    }

    @After("(auditSecurityPointcut() || auditSecurityClassPointcut() || controllerPointcut())")
    public void auditAfter(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AuditSecurity auditSecurity = method.getAnnotation(AuditSecurity.class);
        
        if (auditSecurity == null) {
            auditSecurity = joinPoint.getTarget().getClass().getAnnotation(AuditSecurity.class);
        }

        if (auditSecurity == null) {
            return;
        }

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();
        String action = auditSecurity.action().isEmpty() 
            ? String.format("%s.%s", className, methodName) 
            : auditSecurity.action();
        String resource = auditSecurity.resource().isEmpty() 
            ? className 
            : auditSecurity.resource();

        UUID userId = null;
        String username = null;
        try {
            userId = SecurityUtils
                    .getCurrentUserId();
            username = SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            log.debug("Could not get current user for audit: {}", e.getMessage());
        }

        String userInfo = userId != null 
            ? String.format("userId=%s, username=%s", userId, username != null ? username : "unknown")
            : "anonymous";

        log.info("SECURITY AUDIT - AFTER: action={}, resource={}, user=[{}], status=SUCCESS", 
            action, resource, userInfo);
    }
}

