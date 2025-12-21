package com.kshrd.assessment.dto.answer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

public record SubmitAnswersRequest(
        @NotNull(message = "Assessment ID cannot be null")
        UUID assessmentId,
        
        @Valid
        List<AnswerRequest> answers,
        
        @NotNull(message = "Score cannot be null")
        Double score,
        
        @NotNull(message = "Duration cannot be null")
        Double durationInMinute
) {
}

