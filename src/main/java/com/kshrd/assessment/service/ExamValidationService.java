package com.kshrd.assessment.service;

import com.kshrd.assessment.entity.Assessment;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * Service for validating exam schedules and status.
 * 
 * Timezone Handling:
 * - User input (LocalDate/LocalTime) is accepted as-is without timezone validation
 * - Stored dates/times are timezone-agnostic (LocalDate/LocalTime)
 * - Business logic comparisons use Cambodia timezone (Asia/Phnom_Penh) for current time
 * - All timestamps stored in database use UTC
 */
@Service
public class ExamValidationService {

    private static final ZoneId CAMBODIA_ZONE = ZoneId.of("Asia/Phnom_Penh");

    public boolean isExamActive(Assessment assessment) {
        if (assessment == null) {
            return false;
        }

        if (assessment.getAssessmentDate() == null || 
            assessment.getStartTime() == null || 
            assessment.getEndTime() == null) {
            return false;
        }

        LocalDate today = LocalDate.now(CAMBODIA_ZONE);
        LocalTime currentTime = LocalTime.now(CAMBODIA_ZONE);
        LocalDate assessmentDate = assessment.getAssessmentDate();
        LocalTime startTime = assessment.getStartTime();
        LocalTime endTime = assessment.getEndTime();

        if (!today.equals(assessmentDate)) {
            return false;
        }

        return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
    }

    public boolean isExamEnded(Assessment assessment) {
        if (assessment == null) {
            return false;
        }

        if (assessment.getAssessmentDate() == null || assessment.getEndTime() == null) {
            return false;
        }

        LocalDate today = LocalDate.now(CAMBODIA_ZONE);
        LocalDate assessmentDate = assessment.getAssessmentDate();
        LocalTime endTime = assessment.getEndTime();
        LocalDateTime endDateTime = LocalDateTime.of(assessmentDate, endTime);
        LocalDateTime currentDateTime = LocalDateTime.now(CAMBODIA_ZONE);

        return currentDateTime.isAfter(endDateTime) || today.isAfter(assessmentDate);
    }

    public boolean isExamStarted(Assessment assessment) {
        if (assessment == null) {
            return false;
        }

        if (assessment.getAssessmentDate() == null || assessment.getStartTime() == null) {
            return false;
        }

        LocalDate today = LocalDate.now(CAMBODIA_ZONE);
        LocalDate assessmentDate = assessment.getAssessmentDate();
        LocalTime startTime = assessment.getStartTime();
        LocalDateTime startDateTime = LocalDateTime.of(assessmentDate, startTime);
        LocalDateTime currentDateTime = LocalDateTime.now(CAMBODIA_ZONE);

        return today.equals(assessmentDate) && !currentDateTime.isBefore(startDateTime);
    }

    public void validateExamCanBeStarted(Assessment assessment) {
        if (assessment == null) {
            throw new IllegalStateException("Exam not found");
        }

        if (assessment.getAssessmentDate() == null || 
            assessment.getStartTime() == null || 
            assessment.getEndTime() == null) {
            throw new IllegalStateException("Exam does not have a valid schedule. Cannot start exam without schedule.");
        }

        if (isExamEnded(assessment)) {
            throw new IllegalStateException("Exam has ended. Cannot start an exam that has already ended.");
        }

        if (!isExamStarted(assessment)) {
            LocalDateTime startDateTime = LocalDateTime.of(
                assessment.getAssessmentDate(), 
                assessment.getStartTime()
            );
            throw new IllegalStateException(
                "Exam has not started yet. Exam starts at: " + startDateTime
            );
        }
    }

    public void validateExamCanBeSubmitted(Assessment assessment) {
        if (assessment == null) {
            throw new IllegalStateException("Exam not found");
        }

        if (isExamEnded(assessment)) {
        }
    }
}

