package com.kshrd.assessment.service.serviceImpl;

import com.kshrd.assessment.aop.annotation.LogError;
import com.kshrd.assessment.aop.annotation.LogExecution;
import com.kshrd.assessment.aop.annotation.LogPerformance;
import com.kshrd.assessment.dto.answer.AnswerRequest;
import com.kshrd.assessment.dto.answer.AnswerResponse;
import com.kshrd.assessment.dto.answer.SubmitAllAnswersRequest;
import com.kshrd.assessment.dto.answer.SubmitSingleAnswerRequest;
import com.kshrd.assessment.dto.response.PageRequest;
import com.kshrd.assessment.dto.response.PageResponse;
import com.kshrd.assessment.entity.Answer;
import com.kshrd.assessment.entity.Assessment;
import com.kshrd.assessment.entity.Question;
import com.kshrd.assessment.entity.StudentAssessment;
import com.kshrd.assessment.repository.AnswerRepository;
import com.kshrd.assessment.repository.AssessmentRepository;
import com.kshrd.assessment.repository.QuestionRepository;
import com.kshrd.assessment.repository.StudentAssessmentRepository;
import com.kshrd.assessment.utils.enums.Status;
import com.kshrd.assessment.exception.ResourceNotFoundException;
import com.kshrd.assessment.service.IAnswerService;
import com.kshrd.assessment.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@LogExecution(logParameters = true, logReturnValue = false, description = "Answer Service")
@LogPerformance(thresholdMillis = 1000, description = "Answer Service Performance")
@LogError(logStackTrace = true, description = "Answer Service Error Handling")
public class AnswerServiceImpl implements IAnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AssessmentRepository assessmentRepository;
    private final StudentAssessmentRepository studentAssessmentRepository;

    @Transactional
    public AnswerResponse createAnswer(AnswerRequest request) {
        UUID studentId = SecurityUtils.getCurrentUserId();
        
        if (studentId == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        Question question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new IllegalStateException("Question not found"));

        StudentAssessment studentAssessment = studentAssessmentRepository
                .findByStudentIdAndAssessmentId(studentId, request.assessmentId())
                .orElseThrow(() -> new IllegalStateException("Assessment not assigned or not started"));

        if (studentAssessment.getStatus() != Status.IN_PROGRESS) {
            throw new IllegalStateException("Cannot save answer. Assessment is not in progress");
        }

        UUID attemptId = studentAssessment.getAttemptId();

        // Check if answer already exists for this question in this attempt
        Optional<Answer> existingAnswer = answerRepository.findByAttemptIdAndQuestion_Question_id(attemptId, request.questionId());
        
        Answer answer;
        if (existingAnswer.isPresent()) {
            answer = existingAnswer.get();
            answer.setAnswer(request.answer());
        } else {
            answer = new Answer();
            answer.setAttemptId(attemptId);
            answer.setQuestion(question);
            answer.setAnswer(request.answer());
        }

        Answer saved = answerRepository.save(answer);
        return toResponse(saved, studentAssessment);
    }

    @Transactional
    public AnswerResponse updateAnswer(UUID answerId, AnswerRequest request) {
        UUID studentId = SecurityUtils.getCurrentUserId();
        
        if (studentId == null) {
            throw new IllegalStateException("User is not authenticated");
        }


        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalStateException("Answer not found"));

        StudentAssessment studentAssessment = studentAssessmentRepository.findById(answer.getAttemptId())
                .orElseThrow(() -> new IllegalStateException("Attempt not found"));

        if (!studentAssessment.getStudentId().equals(studentId)) {
            throw new IllegalStateException("You can only update your own answers");
        }

        if (studentAssessment.getStatus() != Status.IN_PROGRESS) {
            throw new IllegalStateException("Cannot update answer. Assessment is not in progress");
        }

        answer.setAnswer(request.answer());

        Answer saved = answerRepository.save(answer);
        return toResponse(saved, studentAssessment);
    }

    @Transactional
    public AnswerResponse saveOrUpdateAnswer(AnswerRequest request) {
        UUID studentId = SecurityUtils.getCurrentUserId();
        
        if (studentId == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        StudentAssessment studentAssessment = studentAssessmentRepository
                .findByStudentIdAndAssessmentId(studentId, request.assessmentId())
                .orElseThrow(() -> new IllegalStateException("Assessment not assigned or not started"));

        UUID attemptId = studentAssessment.getAttemptId();
        Optional<Answer> existingAnswer = answerRepository.findByAttemptIdAndQuestion_Question_id(attemptId, request.questionId());
        
        if (existingAnswer.isPresent()) {
            return updateAnswer(existingAnswer.get().getAnswerId(), request);
        } else {
            return createAnswer(request);
        }
    }

    @Override
    @Transactional
    public List<AnswerResponse> submitAllAnswers(SubmitAllAnswersRequest request) {
        UUID studentId = SecurityUtils.getCurrentUserId();
        
        if (studentId == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        StudentAssessment studentAssessment = studentAssessmentRepository
                .findByStudentIdAndAssessmentId(studentId, request.assessmentId())
                .orElseThrow(() -> new IllegalStateException("Assessment not assigned or not started"));

        if (studentAssessment.getStatus() != Status.IN_PROGRESS) {
            throw new IllegalStateException("Cannot save answers. Assessment is not in progress");
        }

        UUID attemptId = studentAssessment.getAttemptId();
        List<AnswerResponse> responses = new java.util.ArrayList<>();

        if (request.answers() != null && !request.answers().isEmpty()) {
            for (AnswerRequest answerRequest : request.answers()) {
                Question question = questionRepository.findById(answerRequest.questionId())
                        .orElseThrow(() -> new IllegalStateException("Question not found: " + answerRequest.questionId()));

                Optional<Answer> existingAnswer = answerRepository.findByAttemptIdAndQuestion_Question_id(attemptId, answerRequest.questionId());
                
                Answer answer;
                if (existingAnswer.isPresent()) {
                    answer = existingAnswer.get();
                    answer.setAnswer(answerRequest.answer());
                } else {
                    answer = new Answer();
                    answer.setAttemptId(attemptId);
                    answer.setQuestion(question);
                    answer.setAnswer(answerRequest.answer());
                }

                Answer saved = answerRepository.save(answer);
                responses.add(toResponse(saved, studentAssessment));
            }
        }

        return responses;
    }

    @Override
    @Transactional
    public AnswerResponse submitSingleAnswer(SubmitSingleAnswerRequest request) {
        UUID studentId = SecurityUtils.getCurrentUserId();
        
        if (studentId == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        Question question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new IllegalStateException("Question not found"));

        StudentAssessment studentAssessment = studentAssessmentRepository
                .findByStudentIdAndAssessmentId(studentId, request.assessmentId())
                .orElseThrow(() -> new IllegalStateException("Assessment not assigned or not started"));

        if (studentAssessment.getStatus() != Status.IN_PROGRESS) {
            throw new IllegalStateException("Cannot save answer. Assessment is not in progress");
        }

        UUID attemptId = studentAssessment.getAttemptId();
        Optional<Answer> existingAnswer = answerRepository.findByAttemptIdAndQuestion_Question_id(attemptId, request.questionId());
        
        Answer answer;
        if (existingAnswer.isPresent()) {
            answer = existingAnswer.get();
            answer.setAnswer(request.answer());
        } else {
            answer = new Answer();
            answer.setAttemptId(attemptId);
            answer.setQuestion(question);
            answer.setAnswer(request.answer());
        }

        Answer saved = answerRepository.save(answer);
        return toResponse(saved, studentAssessment);
    }

    @Transactional(readOnly = true)
    public List<AnswerResponse> getAnswersByAssessment(UUID assessmentId) {
        List<StudentAssessment> attempts = studentAssessmentRepository.findByAssessmentId(assessmentId);
        List<UUID> attemptIds = attempts.stream()
                .map(StudentAssessment::getAttemptId)
                .collect(Collectors.toList());
        
        if (attemptIds.isEmpty()) {
            throw new ResourceNotFoundException("No answers found for assessment", assessmentId.toString());
        }
        
        List<Answer> allAnswers = attemptIds.stream()
                .flatMap(attemptId -> answerRepository.findByAttemptId(attemptId).stream())
                .collect(Collectors.toList());
        
        return allAnswers.stream()
                .map(answer -> {
                    StudentAssessment attempt = attempts.stream()
                            .filter(a -> a.getAttemptId().equals(answer.getAttemptId()))
                            .findFirst()
                            .orElseThrow();
                    return toResponse(answer, attempt);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AnswerResponse> getAnswersByAssessment(UUID assessmentId, PageRequest pageRequest) {
        List<StudentAssessment> attempts = studentAssessmentRepository.findByAssessmentId(assessmentId);
        List<UUID> attemptIds = attempts.stream()
                .map(StudentAssessment::getAttemptId)
                .collect(Collectors.toList());
        
        if (attemptIds.isEmpty()) {
            throw new ResourceNotFoundException("No answers found for assessment", assessmentId.toString());
        }
        
        var pageable = pageRequest.toPageable();
        List<Answer> allAnswers = attemptIds.stream()
                .flatMap(attemptId -> answerRepository.findByAttemptId(attemptId).stream())
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allAnswers.size());
        List<Answer> pagedAnswers = start < allAnswers.size() ? allAnswers.subList(start, end) : java.util.Collections.emptyList();
        
        List<AnswerResponse> content = pagedAnswers.stream()
                .map(answer -> {
                    StudentAssessment attempt = attempts.stream()
                            .filter(a -> a.getAttemptId().equals(answer.getAttemptId()))
                            .findFirst()
                            .orElseThrow();
                    return toResponse(answer, attempt);
                })
                .collect(Collectors.toList());
        
        return PageResponse.of(new org.springframework.data.domain.PageImpl<>(content, pageable, (long) allAnswers.size()));
    }

    @Transactional(readOnly = true)
    public List<AnswerResponse> getMyAnswersByAssessment(UUID assessmentId) {
        UUID studentId = SecurityUtils.getCurrentUserId();
        
        if (studentId == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        StudentAssessment studentAssessment = studentAssessmentRepository
                .findByStudentIdAndAssessmentId(studentId, assessmentId)
                .orElseThrow(() -> new IllegalStateException("Assessment not found"));

        UUID attemptId = studentAssessment.getAttemptId();
        List<Answer> answers = answerRepository.findByAttemptId(attemptId);
        
        if (answers.isEmpty()) {
            throw new ResourceNotFoundException("No answers found for assessment", assessmentId.toString());
        }
        
        return answers.stream()
                .map(answer -> toResponse(answer, studentAssessment))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AnswerResponse> getMyAnswersByAssessment(UUID assessmentId, PageRequest pageRequest) {
        UUID studentId = SecurityUtils.getCurrentUserId();
        
        if (studentId == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        StudentAssessment studentAssessment = studentAssessmentRepository
                .findByStudentIdAndAssessmentId(studentId, assessmentId)
                .orElseThrow(() -> new IllegalStateException("Assessment not found"));

        UUID attemptId = studentAssessment.getAttemptId();
        var pageable = pageRequest.toPageable();
        var page = answerRepository.findByAttemptId(attemptId, pageable);
        
        if (page.getContent().isEmpty() && page.getTotalElements() == 0) {
            throw new ResourceNotFoundException("No answers found for assessment", assessmentId.toString());
        }
        
        List<AnswerResponse> content = page.getContent().stream()
                .map(answer -> toResponse(answer, studentAssessment))
                .collect(Collectors.toList());
        return PageResponse.of(new org.springframework.data.domain.PageImpl<>(content, pageable, page.getTotalElements()));
    }

    @Transactional(readOnly = true)
    public AnswerResponse getAnswerByQuestion(UUID questionId) {
        UUID studentId = SecurityUtils.getCurrentUserId();
        
        if (studentId == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        List<StudentAssessment> attempts = studentAssessmentRepository.findByStudentId(studentId);
        for (StudentAssessment attempt : attempts) {
            Optional<Answer> answer = answerRepository.findByAttemptIdAndQuestion_Question_id(attempt.getAttemptId(), questionId);
            if (answer.isPresent()) {
                return toResponse(answer.get(), attempt);
            }
        }

        throw new ResourceNotFoundException("Answer not found for question", questionId.toString());
    }

    public boolean isAnswerOwner(UUID answerId) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        
        if (currentUserId == null) {
            return false;
        }

        return answerRepository.findById(answerId)
                .map(answer -> {
                    StudentAssessment attempt = studentAssessmentRepository.findById(answer.getAttemptId())
                            .orElse(null);
                    return attempt != null && attempt.getStudentId().equals(currentUserId);
                })
                .orElse(false);
    }

    private AnswerResponse toResponse(Answer answer, StudentAssessment studentAssessment) {
        return new AnswerResponse(
                answer.getAnswerId(),
                studentAssessment.getStudentId(),
                answer.getQuestion().getQuestion_id(),
                studentAssessment.getAssessmentId(),
                answer.getAnswer(),
                answer.getScore(),
                answer.getSavedAt() != null ? answer.getSavedAt().toLocalDateTime() : null,
                answer.getSavedAt() != null ? answer.getSavedAt().toLocalDateTime() : null
        );
    }
}
