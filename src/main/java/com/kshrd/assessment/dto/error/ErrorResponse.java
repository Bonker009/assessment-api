package com.kshrd.assessment.dto.error;

import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RequiredArgsConstructor
@Getter
@Setter
public class ErrorResponse {
    
    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");
    
    private String status;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private String errorCode;

    public ErrorResponse(String status, String message, String path, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now(UTC_ZONE);
    }
    
    public ErrorResponse(String status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now(UTC_ZONE);
    }
}
