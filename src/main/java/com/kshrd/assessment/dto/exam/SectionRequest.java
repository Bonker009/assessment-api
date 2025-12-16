package com.kshrd.assessment.dto.exam;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record SectionRequest(
        @NotBlank(message = "Section name cannot be blank")
        String sectionName,
        
        @Valid
        List<QuestionRequest> questions
) {
}
