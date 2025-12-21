package com.kshrd.assessment.exception;

import com.kshrd.assessment.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        
        String path = request.getDescription(false).replace("uri=", "");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message, HttpStatus.BAD_REQUEST.value(), "VALIDATION_FAILED", path));
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        String message = ex.getMessage();
        String path = request.getDescription(false).replace("uri=", "");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message, HttpStatus.BAD_REQUEST.value(), "VALIDATION_FAILED", path));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), "INVALID_ARGUMENT", path));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalStateException(
            IllegalStateException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), "ILLEGAL_STATE", path));
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            Exception ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Access Denied: " + ex.getMessage(), 
                        HttpStatus.FORBIDDEN.value(), "ACCESS_DENIED", path));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Unauthorized: " + ex.getMessage(), 
                        HttpStatus.UNAUTHORIZED.value(), "UNAUTHORIZED", path));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        
        if (ex.getMessage() != null && (
                ex.getMessage().contains("not found") ||
                ex.getMessage().contains("cannot") ||
                ex.getMessage().contains("already exists") ||
                ex.getMessage().contains("invalid"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), "BUSINESS_LOGIC_ERROR", path));
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred",
                        HttpStatus.INTERNAL_SERVER_ERROR.value(), "RUNTIME_ERROR", path));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(
            Exception ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred",
                        HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_SERVER_ERROR", path));
    }
}
