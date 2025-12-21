package com.kshrd.assessment.service.serviceImpl;

import com.kshrd.assessment.dto.answer.AnswerRequest;
import com.kshrd.assessment.dto.answer.SubmitAnswersRequest;
import com.kshrd.assessment.dto.studentassessment.GradeAssessmentRequest;
import com.kshrd.assessment.dto.studentassessment.StudentAssessmentRequest;
import com.kshrd.assessment.dto.studentassessment.StudentAssessmentResponse;
import com.kshrd.assessment.dto.studentassessment.SubmitAssessmentRequest;
import com.kshrd.assessment.entity.Assessment;
import com.kshrd.assessment.entity.StudentAssessment;
import com.kshrd.assessment.entity.StudentAssessmentId;
import com.kshrd.assessment.mapper.IStudentAssessmentMapper;
import com.kshrd.assessment.repository.AssessmentRepository;
import com.kshrd.assessment.repository.StudentAssessmentRepository;
import com.kshrd.assessment.service.ExamValidationService;
import com.kshrd.assessment.service.IAnswerService;
import com.kshrd.assessment.service.IStudentAssessmentService;
import com.kshrd.assessment.utils.SecurityUtils;
import com.kshrd.assessment.utils.enums.Status;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StudentAssessmentServiceImpl implements IStudentAssessmentService {
    
    private final StudentAssessmentRepository studentAssessmentRepository;
    private final AssessmentRepository assessmentRepository;
    private final IStudentAssessmentMapper mapper;
    private final ExamValidationService examValidationService;
    private final IAnswerService answerService;
    
    @Transactional
    public StudentAssessmentResponse assignAssessment(StudentAssessmentRequest request) {
        UUID studentId = request.studentId();
        UUID assessmentId = request.assessmentId();
        
        if (studentAssessmentRepository.existsByStudentIdAndAssessmentId(studentId, assessmentId)) {
            throw new IllegalStateException("Assessment already assigned to this student");
        }
        
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new IllegalStateException("Assessment not found"));
        
        if (!Boolean.TRUE.equals(assessment.getIsPublished())) {
            throw new IllegalStateException("Cannot assign unpublished assessment to student");
        }
        
        StudentAssessment studentAssessment = new StudentAssessment();
        studentAssessment.setStudentId(studentId);
        studentAssessment.setAssessmentId(assessmentId);
        studentAssessment.setStatus(Status.ASSIGNED);
        studentAssessment.setScore(0.0);
        studentAssessment.setTotalScore(0.0);
        studentAssessment.setDurationInMinute(0.0);
        studentAssessment.setGradingStatus("not graded");
        
        StudentAssessment saved = studentAssessmentRepository.save(studentAssessment);
        return mapper.toResponse(saved);
    }
    
    @Transactional
    public StudentAssessmentResponse startAssessment(UUID assessmentId) {
        UUID studentId = SecurityUtils.getCurrentUserId();
        
        StudentAssessment studentAssessment = studentAssessmentRepository
                .findByStudentIdAndAssessmentId(studentId, assessmentId)
                .orElseThrow(() -> new IllegalStateException("Assessment not assigned to student"));
        
        if (studentAssessment.getStatus() != Status.ASSIGNED) {
            throw new IllegalStateException("Assessment cannot be started. Current status: " + studentAssessment.getStatus());
        }
        
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new IllegalStateException("Assessment not found"));
        
        examValidationService.validateExamCanBeStarted(assessment);
        
        studentAssessment.setStatus(Status.IN_PROGRESS);
        studentAssessment.setJoinAt(LocalDateTime.now());
        
        StudentAssessment saved = studentAssessmentRepository.save(studentAssessment);
        return mapper.toResponse(saved);
    }
    
    @Transactional
    public StudentAssessmentResponse submitAssessment(SubmitAssessmentRequest request) {
        UUID studentId = SecurityUtils.getCurrentUserId();
        
        StudentAssessment studentAssessment = studentAssessmentRepository
                .findByStudentIdAndAssessmentId(studentId, request.assessmentId())
                .orElseThrow(() -> new IllegalStateException("Assessment not found"));
        
        if (studentAssessment.getStatus() != Status.IN_PROGRESS) {
            throw new IllegalStateException("Assessment cannot be submitted. Current status: " + studentAssessment.getStatus());
        }
        
        Assessment assessment = assessmentRepository.findById(request.assessmentId())
                .orElseThrow(() -> new IllegalStateException("Assessment not found"));
        
        examValidationService.validateExamCanBeSubmitted(assessment);
        
        if (examValidationService.isExamEnded(assessment)) {
            studentAssessment.setStatus(Status.EXPIRED);
            studentAssessment.setGradingStatus("auto-expired");
        } else {
            studentAssessment.setStatus(Status.SUBMITTED);
            studentAssessment.setGradingStatus("pending");
        }
        
        studentAssessment.setScore(request.score());
        studentAssessment.setTotalScore(request.score());
        studentAssessment.setDurationInMinute(request.durationInMinute());
        studentAssessment.setSubmittedAt(LocalDateTime.now());
        
        StudentAssessment saved = studentAssessmentRepository.save(studentAssessment);
        return mapper.toResponse(saved);
    }
    
    @Transactional
    public StudentAssessmentResponse submitAssessmentWithAnswers(SubmitAnswersRequest request) {
        UUID studentId = SecurityUtils.getCurrentUserId();
        
        StudentAssessment studentAssessment = studentAssessmentRepository
                .findByStudentIdAndAssessmentId(studentId, request.assessmentId())
                .orElseThrow(() -> new IllegalStateException("Assessment not found"));
        
        if (studentAssessment.getStatus() != Status.IN_PROGRESS) {
            throw new IllegalStateException("Assessment cannot be submitted. Current status: " + studentAssessment.getStatus());
        }
        
        Assessment assessment = assessmentRepository.findById(request.assessmentId())
                .orElseThrow(() -> new IllegalStateException("Assessment not found"));
        
        examValidationService.validateExamCanBeSubmitted(assessment);
        
        // Save all answers if provided
        if (request.answers() != null && !request.answers().isEmpty()) {
            for (AnswerRequest answerRequest : request.answers()) {
                // Ensure assessmentId matches
                AnswerRequest answerWithAssessment = new AnswerRequest(
                        answerRequest.questionId(),
                        request.assessmentId(),
                        answerRequest.answer(),
                        answerRequest.score()
                );
                answerService.saveOrUpdateAnswer(answerWithAssessment);
            }
        }
        
        if (examValidationService.isExamEnded(assessment)) {
            studentAssessment.setStatus(Status.EXPIRED);
            studentAssessment.setGradingStatus("auto-expired");
        } else {
            studentAssessment.setStatus(Status.SUBMITTED);
            studentAssessment.setGradingStatus("pending");
        }
        
        studentAssessment.setScore(request.score());
        studentAssessment.setTotalScore(request.score());
        studentAssessment.setDurationInMinute(request.durationInMinute());
        studentAssessment.setSubmittedAt(LocalDateTime.now());
        
        StudentAssessment saved = studentAssessmentRepository.save(studentAssessment);
        return mapper.toResponse(saved);
    }
    
    public List<StudentAssessmentResponse> getMyAssessments() {
        UUID studentId = SecurityUtils.getCurrentUserId();
        
        if (studentId == null) {
            throw new IllegalStateException("User is not authenticated");
        }
        
        return studentAssessmentRepository.findByStudentId(studentId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
    
    public Optional<StudentAssessmentResponse> getMyAssessment(UUID assessmentId) {
        UUID studentId = SecurityUtils.getCurrentUserId();
        
        if (studentId == null) {
            throw new IllegalStateException("User is not authenticated");
        }
        
        return studentAssessmentRepository.findByStudentIdAndAssessmentId(studentId, assessmentId)
                .map(mapper::toResponse);
    }
    
    @Transactional
    public StudentAssessmentResponse gradeAssessment(GradeAssessmentRequest request) {
        StudentAssessment studentAssessment = studentAssessmentRepository
                .findByStudentIdAndAssessmentId(request.studentId(), request.assessmentId())
                .orElseThrow(() -> new IllegalStateException("Assessment not found"));
        
        studentAssessment.setScore(request.score());
        studentAssessment.setTotalScore(request.score());
        studentAssessment.setGradingStatus(request.gradingStatus());
        studentAssessment.setStatus(Status.GRADED);
        
        StudentAssessment saved = studentAssessmentRepository.save(studentAssessment);
        return mapper.toResponse(saved);
    }
    
    public Optional<StudentAssessment> findById(UUID studentId, UUID assessmentId) {
        StudentAssessmentId id = new StudentAssessmentId(studentId, assessmentId);
        return studentAssessmentRepository.findById(id);
    }
}
