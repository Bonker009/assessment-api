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
import com.kshrd.assessment.service.ExamValidationService;
import com.kshrd.assessment.service.IExamService;
import com.kshrd.assessment.utils.SecurityUtils;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ExamServiceImpl implements IExamService {
    private static final Logger log = LoggerFactory.getLogger(ExamServiceImpl.class);

    private final AssessmentRepository assessmentRepository;
    private final SectionRepository sectionRepository;
    private final QuestionRepository questionRepository;
    private final IExamMapper examMapper;
    private final ExamValidationService examValidationService;

    @Transactional
    public ExamResponse createExam(ExamRequest request) {
        Assessment assessment = getAssessment(request);
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
                                        question.setPoints(questionRequest.points());
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
        saved = assessmentRepository.findByIdWithSections(saved.getAssessment_id())
                .orElse(saved);
        if (saved != null) {
            initializeQuestions(saved);
        }
        return examMapper.toResponse(saved);
    }

    private static @NonNull Assessment getAssessment(ExamRequest request) {
        Assessment assessment = new Assessment();
        assessment.setName(request.name());
        assessment.setIsQuiz(request.isQuiz());
        assessment.setSubjectId(request.subjectId());
        if (request.schedule() != null) {
            assessment.setAssessmentDate(request.schedule().assessmentDate());
            assessment.setStartTime(request.schedule().startTime());
            assessment.setEndTime(request.schedule().endTime());
            assessment.setIsPublished(request.schedule().isPublished() != null ? request.schedule().isPublished() : false);
        } else {
            assessment.setIsPublished(false);
        }
        return assessment;
    }

    @Transactional(readOnly = true)
    public Optional<ExamResponse> getExamById(UUID examId) {
        return assessmentRepository.findByIdWithSections(examId)
                .map(assessment -> {
                    if (assessment.getSections() != null && !assessment.getSections().isEmpty()) {
                        List<Section> sectionsWithQuestions = sectionRepository.findByAssessmentIdWithQuestions(examId);
                        Map<UUID, Section> sectionMap = sectionsWithQuestions.stream()
                                .collect(Collectors.toMap(Section::getSection_id, s -> s));
                        
                        assessment.getSections().forEach(section -> {
                            Section sectionWithQuestions = sectionMap.get(section.getSection_id());
                            if (sectionWithQuestions != null && sectionWithQuestions.getQuestions() != null) {
                                section.setQuestions(sectionWithQuestions.getQuestions());
                            }
                        });
                    }
                    return examMapper.toResponse(assessment);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponse>getMyExams() {
        UUID currentUser =  SecurityUtils.getCurrentUserId();
        List<Assessment> assessments = assessmentRepository.findByCreatedBy(currentUser);
        return assessments.stream()
                .map(assessment -> {
                    Long totalSections = sectionRepository.countByAssessment_Assessment_id(assessment.getAssessment_id());
                    Long totalQuestions = questionRepository.countByAssessmentId(assessment.getAssessment_id());
                    return examMapper.toResponseWithoutSections(assessment, totalSections, totalQuestions);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExamResponse> getAllExams() {
        List<Assessment> assessments = assessmentRepository.findAll();
        return assessments.stream()
                .map(assessment -> {
                    Long totalSections = sectionRepository.countByAssessment_Assessment_id(assessment.getAssessment_id());
                    Long totalQuestions = questionRepository.countByAssessmentId(assessment.getAssessment_id());
                    return examMapper.toResponseWithoutSections(assessment, totalSections, totalQuestions);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExamResponse> getActiveExams() {
        try {
            List<Assessment> activeAssessments = assessmentRepository.findActiveExamsNative();
            return activeAssessments.stream()
                    .map(assessment -> {
                        Long totalSections = sectionRepository.countByAssessment_Assessment_id(assessment.getAssessment_id());
                        Long totalQuestions = questionRepository.countByAssessmentId(assessment.getAssessment_id());
                        return examMapper.toResponseWithoutSections(assessment, totalSections, totalQuestions);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Native query failed, falling back to Java filtering: {}", e.getMessage());
            List<Assessment> publishedAssessments = assessmentRepository.findPublishedExamsWithSchedule();
            return publishedAssessments.stream()
                    .filter(examValidationService::isExamActive)
                    .map(assessment -> {
                        Long totalSections = sectionRepository.countByAssessment_Assessment_id(assessment.getAssessment_id());
                        Long totalQuestions = questionRepository.countByAssessmentId(assessment.getAssessment_id());
                        return examMapper.toResponseWithoutSections(assessment, totalSections, totalQuestions);
                    })
                    .collect(Collectors.toList());
        }
    }
    
    private void initializeQuestions(Assessment assessment) {
        if (assessment.getSections() != null) {
            for (Section section : assessment.getSections()) {
                Hibernate.initialize(section.getQuestions());
            }
        }
    }
    
    private void initializeQuestionsForAssessments(List<Assessment> assessments) {
        if (assessments == null || assessments.isEmpty()) {
            return;
        }
        
        assessments.forEach(assessment -> {
            if (assessment.getSections() != null && !assessment.getSections().isEmpty()) {
                UUID assessmentId = assessment.getAssessment_id();
                List<Section> sectionsWithQuestions = sectionRepository.findByAssessmentIdWithQuestions(assessmentId);
                
                Map<UUID, Section> sectionMap = sectionsWithQuestions.stream()
                        .collect(Collectors.toMap(Section::getSection_id, s -> s));
                
                assessment.getSections().forEach(section -> {
                    Section sectionWithQuestions = sectionMap.get(section.getSection_id());
                    if (sectionWithQuestions != null && sectionWithQuestions.getQuestions() != null) {
                        section.setQuestions(sectionWithQuestions.getQuestions());
                    }
                });
            }
        });
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

        if (request.schedule() != null) {
            assessment.setAssessmentDate(request.schedule().assessmentDate());
            assessment.setStartTime(request.schedule().startTime());
            assessment.setEndTime(request.schedule().endTime());
            if (request.schedule().isPublished() != null) {
                assessment.setIsPublished(request.schedule().isPublished());
            }
        }

        Assessment saved = assessmentRepository.save(assessment);
        saved = assessmentRepository.findByIdWithSections(saved.getAssessment_id())
                .orElse(saved);
        if (saved != null) {
            initializeQuestions(saved);
        }
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
        saved = assessmentRepository.findByIdWithSections(saved.getAssessment_id())
                .orElse(saved);
        if (saved != null) {
            initializeQuestions(saved);
        }
        return examMapper.toResponse(saved);
    }

    @Transactional
    public ExamResponse unpublishExam(UUID examId) {
        Assessment assessment = assessmentRepository.findById(examId)
                .orElseThrow(() -> new IllegalStateException("Exam not found"));

        assessment.setIsPublished(false);
        Assessment saved = assessmentRepository.save(assessment);
        saved = assessmentRepository.findByIdWithSections(saved.getAssessment_id())
                .orElse(saved);
        if (saved != null) {
            initializeQuestions(saved);
        }
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
        saved = assessmentRepository.findByIdWithSections(saved.getAssessment_id())
                .orElse(saved);
        if (saved != null) {
            initializeQuestions(saved);
        }
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
        question.setPoints(request.points());
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
