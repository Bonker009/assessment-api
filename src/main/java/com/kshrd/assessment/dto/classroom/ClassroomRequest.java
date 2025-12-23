package com.kshrd.assessment.dto.classroom;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ClassroomRequest(
        @NotBlank(message = "Class name cannot be blank")
        String className,
        
        String description,
        
        UUID subjectId,
        
        @NotNull(message = "Teacher ID cannot be null")
        UUID teacherId
) {
}

