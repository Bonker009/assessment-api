package com.kshrd.assessment.service;

import com.kshrd.assessment.entity.Assessment;
import com.kshrd.assessment.entity.StudentAssessment;
import com.kshrd.assessment.repository.AssessmentRepository;
import com.kshrd.assessment.repository.StudentAssessmentRepository;
import com.kshrd.assessment.utils.enums.Status;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ExamSchedulerService {

    private final StudentAssessmentRepository studentAssessmentRepository;
    private final AssessmentRepository assessmentRepository;

    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void expireInProgressAssessments() {
        log.debug("Running scheduled task: expireInProgressAssessments");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        
        List<Assessment> endedAssessments = assessmentRepository.findAll().stream()
                .filter(assessment -> {
                    if (assessment.getAssessmentDate() == null || 
                        assessment.getEndTime() == null || 
                        !assessment.getIsPublished()) {
                        return false;
                    }
                    
                    LocalDate assessmentDate = assessment.getAssessmentDate();
                    LocalTime endTime = assessment.getEndTime();
                    LocalDateTime endDateTime = LocalDateTime.of(assessmentDate, endTime);
                    
                    return now.isAfter(endDateTime) || 
                           (today.isAfter(assessmentDate));
                })
                .toList();
        
        if (endedAssessments.isEmpty()) {
            log.debug("No ended assessments found");
            return;
        }
        
        int expiredCount = 0;
        for (Assessment assessment : endedAssessments) {
            List<StudentAssessment> inProgressAssessments = 
                    studentAssessmentRepository.findByAssessmentId(assessment.getAssessment_id())
                            .stream()
                            .filter(sa -> sa.getStatus() == Status.IN_PROGRESS)
                            .toList();
            
            for (StudentAssessment studentAssessment : inProgressAssessments) {
                studentAssessment.setStatus(Status.EXPIRED);
                studentAssessment.setSubmittedAt(now);
                
                if (studentAssessment.getScore() == null || studentAssessment.getScore() == 0.0) {
                    studentAssessment.setScore(0.0);
                    studentAssessment.setTotalScore(0.0);
                }
                
                if (studentAssessment.getJoinAt() != null) {
                    long minutes = java.time.Duration.between(
                            studentAssessment.getJoinAt(), 
                            now
                    ).toMinutes();
                    studentAssessment.setDurationInMinute((double) minutes);
                }
                
                studentAssessment.setGradingStatus("auto-expired");
                studentAssessmentRepository.save(studentAssessment);
                expiredCount++;
                
                log.info("Auto-expired assessment for student {} and exam {}", 
                        studentAssessment.getStudentId(), 
                        studentAssessment.getAssessmentId());
            }
        }
        
        if (expiredCount > 0) {
            log.info("Expired {} in-progress assessments", expiredCount);
        }
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void dailyCleanup() {
        log.info("Running daily cleanup task");
    }
}

