package com.kshrd.assessment.service;

import com.kshrd.assessment.entity.Assessment;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class ExamValidationService {

    public boolean isExamActive(Assessment assessment) {
        if (assessment == null) {
            return false;
        }

        if (!Boolean.TRUE.equals(assessment.getIsPublished())) {
            return false;
        }

        if (assessment.getAssessmentDate() == null || 
            assessment.getStartTime() == null || 
            assessment.getEndTime() == null) {
            return false;
        }

        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
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

        LocalDate today = LocalDate.now();
        LocalDate assessmentDate = assessment.getAssessmentDate();
        LocalTime endTime = assessment.getEndTime();
        LocalDateTime endDateTime = LocalDateTime.of(assessmentDate, endTime);
        LocalDateTime currentDateTime = LocalDateTime.now();

        return currentDateTime.isAfter(endDateTime) || today.isAfter(assessmentDate);
    }

    public boolean isExamStarted(Assessment assessment) {
        if (assessment == null) {
            return false;
        }

        if (assessment.getAssessmentDate() == null || assessment.getStartTime() == null) {
            return false;
        }

        LocalDate today = LocalDate.now();
        LocalDate assessmentDate = assessment.getAssessmentDate();
        LocalTime startTime = assessment.getStartTime();
        LocalDateTime startDateTime = LocalDateTime.of(assessmentDate, startTime);
        LocalDateTime currentDateTime = LocalDateTime.now();

        return today.equals(assessmentDate) && !currentDateTime.isBefore(startDateTime);
    }

    public void validateExamCanBeStarted(Assessment assessment) {
        if (assessment == null) {
            throw new IllegalStateException("Exam not found");
        }

        if (!Boolean.TRUE.equals(assessment.getIsPublished())) {
            throw new IllegalStateException("Exam is not published. Cannot start unpublished exam.");
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

