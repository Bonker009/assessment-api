package com.kshrd.assessment.dto.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    
    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> ok(T data, String message) {
        return ResponseEntity.ok(ApiResponse.success(data, message));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(data, "Resource created successfully", HttpStatus.CREATED.value()));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(data, message, HttpStatus.CREATED.value()));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> noContent() {
        ApiResponse<T> response = ApiResponse.<T>success("Operation completed successfully", HttpStatus.NO_CONTENT.value());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> noContent(String message) {
        ApiResponse<T> response = ApiResponse.<T>success(message, HttpStatus.NO_CONTENT.value());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message, HttpStatus.BAD_REQUEST.value()));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message, String errorCode) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message, HttpStatus.BAD_REQUEST.value(), errorCode));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> unauthorized(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(message, HttpStatus.UNAUTHORIZED.value()));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> unauthorized(String message, String errorCode) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(message, HttpStatus.UNAUTHORIZED.value(), errorCode));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> forbidden(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(message, HttpStatus.FORBIDDEN.value()));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> forbidden(String message, String errorCode) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(message, HttpStatus.FORBIDDEN.value(), errorCode));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(message, HttpStatus.NOT_FOUND.value()));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message, String errorCode) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(message, HttpStatus.NOT_FOUND.value(), errorCode));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> conflict(String message) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(message, HttpStatus.CONFLICT.value()));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> conflict(String message, String errorCode) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(message, HttpStatus.CONFLICT.value(), errorCode));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> internalServerError(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(message, HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> internalServerError(String message, String errorCode) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(message, HttpStatus.INTERNAL_SERVER_ERROR.value(), errorCode));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> status(HttpStatus status, ApiResponse<T> response) {
        return ResponseEntity.status(status).body(response);
    }
}

