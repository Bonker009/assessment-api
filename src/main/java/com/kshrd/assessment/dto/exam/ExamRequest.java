package com.kshrd.assessment.dto.exam;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record ExamRequest(
        @NotBlank(message = "Name cannot be blank")
        String name,
        
        @NotNull(message = "IsQuiz cannot be null")
        Boolean isQuiz,
        
        @NotNull(message = "Subject ID cannot be null")
        UUID subjectId,
        
        LocalDate assessmentDate,
        LocalTime startTime,
        LocalTime endTime,
        
        @Valid
        List<SectionRequest> sections
) {
}
