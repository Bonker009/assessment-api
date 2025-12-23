package com.kshrd.assessment.dto.classroom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ClassroomResponse(
        UUID classroomId,
        String className,
        String description,
        UUID subjectId,
        UUID teacherId,
        List<UUID> assessmentIds,
        UUID createdBy,
        UUID updatedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

