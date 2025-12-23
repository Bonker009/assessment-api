package com.kshrd.assessment.dto.answer;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

public record AnswerRequest(
        @NotNull(message = "Question ID cannot be null")
        UUID questionId,
        
        @NotNull(message = "Assessment ID cannot be null")
        UUID assessmentId,
        
        Map<String, Object> answer
) {
}

