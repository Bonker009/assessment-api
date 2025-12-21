package com.kshrd.assessment.dto.exam;

import com.kshrd.assessment.utils.enums.QuestionType;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record QuestionUpdateRequest(
        @NotNull(message = "Question type cannot be null")
        QuestionType questionType,
        
        String image,
        
        @NotNull(message = "Question content cannot be null")
        Map<String, Object> questionContent,
        
        Double points
) {
}
