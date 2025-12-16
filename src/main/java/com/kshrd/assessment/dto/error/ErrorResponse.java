package com.kshrd.assessment.dto.error;

import lombok.*;

import java.time.LocalDateTime;
@RequiredArgsConstructor

@Getter
@Setter
public class ErrorResponse {
    private String status;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private String errorCode;

    public ErrorResponse(String status, String message, String path, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
    }
}
