package com.kshrd.assessment.dto.exam;

import java.time.LocalDate;
import java.time.LocalTime;

public record ExamScheduleResponse(
        LocalDate assessmentDate,
        LocalTime startTime,
        LocalTime endTime
) {
}
