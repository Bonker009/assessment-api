package com.kshrd.assessment.service.serviceImpl;

import com.kshrd.assessment.dto.exam.ExamRequest;
import com.kshrd.assessment.dto.exam.ExamResponse;
import com.kshrd.assessment.dto.exam.ExamScheduleRequest;
import com.kshrd.assessment.dto.exam.ExamScheduleResponse;
import com.kshrd.assessment.dto.exam.QuestionResponse;
import com.kshrd.assessment.dto.exam.QuestionUpdateRequest;
import com.kshrd.assessment.dto.exam.SectionResponse;
import com.kshrd.assessment.dto.exam.SectionUpdateRequest;
import com.kshrd.assessment.entity.Assessment;
import com.kshrd.assessment.entity.Question;
import com.kshrd.assessment.entity.Section;
import com.kshrd.assessment.mapper.IExamMapper;
import com.kshrd.assessment.repository.AssessmentRepository;
import com.kshrd.assessment.repository.QuestionRepository;
import com.kshrd.assessment.repository.SectionRepository;
import com.kshrd.assessment.service.IExamService;
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
public class ExamServiceImpl implements IExamService {

    private final AssessmentRepository assessmentRepository;
    private final SectionRepository sectionRepository;
    private final QuestionRepository questionRepository;
    private final IExamMapper examMapper;

    @Transactional
    public ExamResponse createExam(ExamRequest request) {
        Assessment assessment = new Assessment();
        assessment.setName(request.name());
        assessment.setIsQuiz(request.isQuiz());
        assessment.setSubjectId(request.subjectId());
        assessment.setAssessmentDate(request.assessmentDate());
        assessment.setStartTime(request.startTime());
        assessment.setEndTime(request.endTime());
        assessment.setIsPublished(false);
        
        Assessment saved = assessmentRepository.save(assessment);
        
        if (request.sections() != null && !request.sections().isEmpty()) {
            final Assessment assessmentForLambda = saved;
            List<Section> sections = request.sections().stream()
                    .map(sectionRequest -> {
                        Section section = new Section();
                        section.setSectionName(sectionRequest.sectionName());
                        section.setAssessment(assessmentForLambda);
                        
                        if (sectionRequest.questions() != null && !sectionRequest.questions().isEmpty()) {
                            List<Question> questions = sectionRequest.questions().stream()
                                    .map(questionRequest -> {
                                        Question question = new Question();
                                        question.setQuestionType(questionRequest.questionType());
                                        question.setImage(questionRequest.image());
                                        question.setQuestionContent(questionRequest.questionContent());
                                        question.setSection(section);
                                        return question;
                                    })
                                    .collect(Collectors.toList());
                            section.setQuestions(questions);
                        }
                        
                        return section;
                    })
                    .collect(Collectors.toList());
            
            saved.setSections(sections);
            saved = assessmentRepository.save(saved);
        }
        
        return examMapper.toResponse(saved);
    }

    public Optional<ExamResponse> getExamById(UUID examId) {
        return assessmentRepository.findById(examId)
                .map(examMapper::toResponse);
    }

    public List<ExamResponse> getAllExams() {
        return assessmentRepository.findAll()
                .stream()
                .map(examMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<ExamScheduleResponse> getExamSchedule(UUID examId) {
        return assessmentRepository.findById(examId)
                .map(examMapper::toScheduleResponse);
    }

    @Transactional
    public ExamResponse updateExam(UUID examId, ExamRequest request) {
        Assessment assessment = assessmentRepository.findById(examId)
                .orElseThrow(() -> new IllegalStateException("Exam not found"));

        assessment.setName(request.name());
        assessment.setIsQuiz(request.isQuiz());
        assessment.setSubjectId(request.subjectId());
        assessment.setAssessmentDate(request.assessmentDate());
        assessment.setStartTime(request.startTime());
        assessment.setEndTime(request.endTime());

        Assessment saved = assessmentRepository.save(assessment);
        return examMapper.toResponse(saved);
    }

    @Transactional
    public ExamScheduleResponse updateSchedule(UUID examId, ExamScheduleRequest request) {
        Assessment assessment = assessmentRepository.findById(examId)
                .orElseThrow(() -> new IllegalStateException("Exam not found"));

        LocalDateTime assessmentStartDateTime = LocalDateTime.of(request.assessmentDate(), request.startTime());
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(assessmentStartDateTime) || now.isEqual(assessmentStartDateTime)) {
            throw new IllegalStateException("Cannot update schedule after assessment has started");
        }

        assessment.setAssessmentDate(request.assessmentDate());
        assessment.setStartTime(request.startTime());
        assessment.setEndTime(request.endTime());

        Assessment saved = assessmentRepository.save(assessment);
        return examMapper.toScheduleResponse(saved);
    }

    @Transactional
    public ExamResponse publishExam(UUID examId) {
        Assessment assessment = assessmentRepository.findById(examId)
                .orElseThrow(() -> new IllegalStateException("Exam not found"));

        if (assessment.getAssessmentDate() == null || assessment.getStartTime() == null) {
            throw new IllegalStateException("Cannot publish assessment without schedule");
        }

        LocalDateTime assessmentStartDateTime = LocalDateTime.of(assessment.getAssessmentDate(), assessment.getStartTime());
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(assessmentStartDateTime) || now.isEqual(assessmentStartDateTime)) {
            throw new IllegalStateException("Cannot publish assessment after start date");
        }

        assessment.setIsPublished(true);
        Assessment saved = assessmentRepository.save(assessment);
        return examMapper.toResponse(saved);
    }

    @Transactional
    public ExamResponse unpublishExam(UUID examId) {
        Assessment assessment = assessmentRepository.findById(examId)
                .orElseThrow(() -> new IllegalStateException("Exam not found"));

        assessment.setIsPublished(false);
        Assessment saved = assessmentRepository.save(assessment);
        return examMapper.toResponse(saved);
    }

    @Transactional
    public ExamResponse cloneExam(UUID examId) {
        Assessment originalExam = assessmentRepository.findById(examId)
                .orElseThrow(() -> new IllegalStateException("Exam not found"));

        Assessment clonedExam = new Assessment();
        clonedExam.setName(originalExam.getName() + " (Copy)");
        clonedExam.setIsQuiz(originalExam.getIsQuiz());
        clonedExam.setSubjectId(originalExam.getSubjectId());
        clonedExam.setAssessmentDate(originalExam.getAssessmentDate());
        clonedExam.setStartTime(originalExam.getStartTime());
        clonedExam.setEndTime(originalExam.getEndTime());
        clonedExam.setIsPublished(false);

        if (originalExam.getSections() != null && !originalExam.getSections().isEmpty()) {
            List<Section> clonedSections = originalExam.getSections().stream()
                    .map(originalSection -> {
                        Section clonedSection = new Section();
                        clonedSection.setSectionName(originalSection.getSectionName());
                        clonedSection.setAssessment(clonedExam);
                        return clonedSection;
                    })
                    .collect(Collectors.toList());
            clonedExam.setSections(clonedSections);
        }

        Assessment saved = assessmentRepository.save(clonedExam);
        return examMapper.toResponse(saved);
    }

    @Transactional
    public void deleteExam(UUID examId) {
        if (!assessmentRepository.existsById(examId)) {
            throw new IllegalStateException("Exam not found");
        }
        assessmentRepository.deleteById(examId);
    }

    @Transactional
    public void updateSection(UUID sectionId, SectionUpdateRequest request) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new IllegalStateException("Section not found"));
        
        section.setSectionName(request.sectionName());
        sectionRepository.save(section);
    }

    @Transactional
    public void deleteSection(UUID sectionId) {
        if (!sectionRepository.existsById(sectionId)) {
            throw new IllegalStateException("Section not found");
        }
        sectionRepository.deleteById(sectionId);
    }

    @Transactional
    public void updateQuestion(UUID questionId, QuestionUpdateRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalStateException("Question not found"));
        
        question.setQuestionType(request.questionType());
        question.setImage(request.image());
        question.setQuestionContent(request.questionContent());
        questionRepository.save(question);
    }

    @Transactional
    public void deleteQuestion(UUID questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new IllegalStateException("Question not found");
        }
        questionRepository.deleteById(questionId);
    }

    public Optional<SectionResponse> getSectionById(UUID sectionId) {
        return sectionRepository.findById(sectionId)
                .map(examMapper::toSectionResponse);
    }

    public Optional<QuestionResponse> getQuestionById(UUID questionId) {
        return questionRepository.findById(questionId)
                .map(examMapper::toQuestionResponse);
    }
}
