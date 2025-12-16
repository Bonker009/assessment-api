package com.kshrd.assessment.dto.exam;

import java.util.UUID;

public record SectionResponse(
        UUID sectionId,
        String sectionName,
        UUID assessmentId
) {
}
