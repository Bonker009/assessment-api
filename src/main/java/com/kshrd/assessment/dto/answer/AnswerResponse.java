package com.kshrd.assessment.dto.answer;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record AnswerResponse(
        UUID answerId,
        UUID studentId,
        UUID questionId,
        UUID assessmentId,
        Map<String, Object> answer,
        Double score,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

