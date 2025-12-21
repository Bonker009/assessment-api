package com.kshrd.assessment.dto.exam;

import com.kshrd.assessment.utils.enums.QuestionType;
import java.util.Map;
import java.util.UUID;

public record QuestionResponse(
        UUID questionId,
        QuestionType questionType,
        String image,
        Map<String, Object> questionContent,
        UUID sectionId,
        Double points
) {
}
