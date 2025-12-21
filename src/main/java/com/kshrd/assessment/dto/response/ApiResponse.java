package com.kshrd.assessment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private Boolean success;
    
    private Integer statusCode;
    
    private String message;
    
    private T data;
    
    private LocalDateTime timestamp;
    
    private String errorCode;
    
    private String path;
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(200)
                .message("Request successful")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(200)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> success(T data, String message, Integer statusCode) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(statusCode)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(200)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> success(String message, Integer statusCode) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(statusCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> error(String message, Integer statusCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .statusCode(statusCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> error(String message, Integer statusCode, String errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .statusCode(statusCode)
                .message(message)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> error(String message, Integer statusCode, String errorCode, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .statusCode(statusCode)
                .message(message)
                .errorCode(errorCode)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

