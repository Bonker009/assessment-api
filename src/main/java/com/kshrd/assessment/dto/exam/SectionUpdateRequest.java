package com.kshrd.assessment.dto.exam;

import jakarta.validation.constraints.NotBlank;

public record SectionUpdateRequest(
        @NotBlank(message = "Section name cannot be blank")
        String sectionName
) {
}
