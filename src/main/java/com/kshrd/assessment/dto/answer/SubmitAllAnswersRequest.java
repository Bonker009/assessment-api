package com.kshrd.assessment.dto.answer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record SubmitAllAnswersRequest(
        @NotNull(message = "Assessment ID cannot be null")
        UUID assessmentId,
        
        @NotNull(message = "Answers cannot be null")
        @Valid
        List<AnswerRequest> answers
) {
}

