package com.kshrd.assessment.service.serviceImpl;

import com.kshrd.assessment.dto.answer.AnswerRequest;
import com.kshrd.assessment.dto.answer.AnswerResponse;
import com.kshrd.assessment.entity.Answer;
import com.kshrd.assessment.entity.Assessment;
import com.kshrd.assessment.entity.Question;
import com.kshrd.assessment.repository.AnswerRepository;
import com.kshrd.assessment.repository.AssessmentRepository;
import com.kshrd.assessment.repository.QuestionRepository;
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
public class AnswerServiceImpl implements IAnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AssessmentRepository assessmentRepository;

    @Transactional
    public AnswerResponse createAnswer(AnswerRequest request) {
        UUID studentId = SecurityUtils.getCurrentUserId();
        
        if (studentId == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        Question question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new IllegalStateException("Question not found"));

        Assessment assessment = assessmentRepository.findById(request.assessmentId())
                .orElseThrow(() -> new IllegalStateException("Assessment not found"));

        // Check if answer already exists for this question
        Optional<Answer> existingAnswer = answerRepository.findByStudent_idAndQuestion_Question_id(studentId, request.questionId());
        
        Answer answer;
        if (existingAnswer.isPresent()) {
            answer = existingAnswer.get();
            answer.setAnswer(request.answer());
            if (request.score() != null) {
                answer.setScore(request.score());
            }
        } else {
            answer = new Answer();
            answer.setStudent_id(studentId);
            answer.setQuestion(question);
            answer.setAssessment(assessment);
            answer.setAnswer(request.answer());
            answer.setScore(request.score());
        }

        Answer saved = answerRepository.save(answer);
        return toResponse(saved);
    }

    @Transactional
    public AnswerResponse updateAnswer(UUID answerId, AnswerRequest request) {
        UUID studentId = SecurityUtils.getCurrentUserId();
        
        if (studentId == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalStateException("Answer not found"));

        if (!answer.getStudent_id().equals(studentId)) {
            throw new IllegalStateException("You can only update your own answers");
        }

        answer.setAnswer(request.answer());
        if (request.score() != null) {
            answer.setScore(request.score());
        }

        Answer saved = answerRepository.save(answer);
        return toResponse(saved);
    }

    @Transactional
    public AnswerResponse saveOrUpdateAnswer(AnswerRequest request) {
        UUID studentId = SecurityUtils.getCurrentUserId();
        
        if (studentId == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        Optional<Answer> existingAnswer = answerRepository.findByStudent_idAndQuestion_Question_id(studentId, request.questionId());
        
        if (existingAnswer.isPresent()) {
            return updateAnswer(existingAnswer.get().getAnswer_id(), request);
        } else {
            return createAnswer(request);
        }
    }

    @Transactional(readOnly = true)
    public List<AnswerResponse> getAnswersByAssessment(UUID assessmentId) {
        List<Answer> answers = answerRepository.findByAssessment_Assessment_id(assessmentId);
        return answers.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AnswerResponse> getMyAnswersByAssessment(UUID assessmentId) {
        UUID studentId = SecurityUtils.getCurrentUserId();
        
        if (studentId == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        List<Answer> answers = answerRepository.findByStudent_idAndAssessment_Assessment_id(studentId, assessmentId);
        return answers.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AnswerResponse getAnswerByQuestion(UUID questionId) {
        UUID studentId = SecurityUtils.getCurrentUserId();
        
        if (studentId == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        Answer answer = answerRepository.findByStudent_idAndQuestion_Question_id(studentId, questionId)
                .orElseThrow(() -> new IllegalStateException("Answer not found"));

        return toResponse(answer);
    }

    public boolean isAnswerOwner(UUID answerId) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        
        if (currentUserId == null) {
            return false;
        }

        return answerRepository.findById(answerId)
                .map(answer -> answer.getStudent_id().equals(currentUserId))
                .orElse(false);
    }

    private AnswerResponse toResponse(Answer answer) {
        return new AnswerResponse(
                answer.getAnswer_id(),
                answer.getStudent_id(),
                answer.getQuestion().getQuestion_id(),
                answer.getAssessment().getAssessment_id(),
                answer.getAnswer(),
                answer.getScore(),
                answer.getCreatedAt(),
                answer.getUpdatedAt()
        );
    }
}
