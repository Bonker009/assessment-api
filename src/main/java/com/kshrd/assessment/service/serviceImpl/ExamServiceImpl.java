package com.kshrd.assessment.service.serviceImpl;

import com.kshrd.assessment.dto.exam.ExamRequest;
import com.kshrd.assessment.dto.exam.ExamResponse;
import com.kshrd.assessment.dto.exam.ExamScheduleRequest;
import com.kshrd.assessment.dto.exam.ExamScheduleResponse;
import com.kshrd.assessment.dto.exam.QuestionResponse;
import com.kshrd.assessment.dto.exam.QuestionUpdateRequest;
import com.kshrd.assessment.dto.exam.SectionResponse;
import com.kshrd.assessment.dto.exam.SectionUpdateRequest;
import com.kshrd.assessment.dto.response.PageRequest;
import com.kshrd.assessment.dto.response.PageResponse;
import com.kshrd.assessment.entity.Assessment;
import com.kshrd.assessment.entity.Question;
import com.kshrd.assessment.entity.Section;
import com.kshrd.assessment.mapper.IExamMapper;
import com.kshrd.assessment.repository.AssessmentRepository;
import com.kshrd.assessment.repository.QuestionRepository;
import com.kshrd.assessment.repository.SectionRepository;
import com.kshrd.assessment.repository.StudentAssessmentRepository;
import com.kshrd.assessment.aop.annotation.LogError;
import com.kshrd.assessment.aop.annotation.LogExecution;
import com.kshrd.assessment.aop.annotation.LogPerformance;
import com.kshrd.assessment.exception.ResourceNotFoundException;
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
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@LogExecution(logParameters = true, logReturnValue = false, description = "Exam Service")
@LogPerformance(thresholdMillis = 2000, description = "Exam Service Performance")
@LogError(logStackTrace = true, description = "Exam Service Error Handling")
public class ExamServiceImpl implements IExamService {
    private static final Logger log = LoggerFactory.getLogger(ExamServiceImpl.class);
    private static final ZoneId CAMBODIA_ZONE = ZoneId.of("Asia/Phnom_Penh");

    private final AssessmentRepository assessmentRepository;
    private final SectionRepository sectionRepository;
    private final QuestionRepository questionRepository;
    private final StudentAssessmentRepository studentAssessmentRepository;
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
                        section.setSectionName(sectionRequest.sectionName().trim());
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
        assessment.setName(request.name().trim());
        assessment.setIsQuiz(request.isQuiz());
        assessment.setSubjectId(request.subjectId());
        if (request.schedule() != null) {
            assessment.setAssessmentDate(request.schedule().assessmentDate());
            assessment.setStartTime(request.schedule().startTime());
            assessment.setEndTime(request.schedule().endTime());
        }
        return assessment;
    }

    @Transactional(readOnly = true)
    public Optional<ExamResponse> getExamById(UUID examId) {
        Assessment assessment = assessmentRepository.findByIdWithSections(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", examId.toString()));
        
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
        
        return Optional.of(examMapper.toResponse(assessment));
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

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ExamResponse> getMyExams(PageRequest pageRequest) {
        UUID currentUser = SecurityUtils.getCurrentUserId();
        var pageable = pageRequest.toPageable();
        var page = pageRequest.getSearch() != null && !pageRequest.getSearch().trim().isEmpty()
                ? assessmentRepository.findByCreatedByAndNameContainingIgnoreCase(currentUser, pageRequest.getSearch().trim(), pageable)
                : assessmentRepository.findByCreatedBy(currentUser, pageable);
        
        if (page.getContent().isEmpty() && page.getTotalElements() == 0) {
            throw new ResourceNotFoundException("No exams found for user", currentUser.toString());
        }
        
        var content = page.getContent().stream()
                .map(assessment -> {
                    Long totalSections = sectionRepository.countByAssessment_Assessment_id(assessment.getAssessment_id());
                    Long totalQuestions = questionRepository.countByAssessmentId(assessment.getAssessment_id());
                    return examMapper.toResponseWithoutSections(assessment, totalSections, totalQuestions);
                })
                .collect(Collectors.toList());
        return PageResponse.of(content, pageable, page.getTotalElements());
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

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ExamResponse> getAllExams(PageRequest pageRequest) {
        var pageable = pageRequest.toPageable();
        var page = pageRequest.getSearch() != null && !pageRequest.getSearch().trim().isEmpty()
                ? assessmentRepository.findByNameContainingIgnoreCase(pageRequest.getSearch().trim(), pageable)
                : assessmentRepository.findAll(pageable);
        
        if (page.getContent().isEmpty() && page.getTotalElements() == 0) {
            throw new ResourceNotFoundException("No exams found");
        }
        
        var content = page.getContent().stream()
                .map(assessment -> {
                    Long totalSections = sectionRepository.countByAssessment_Assessment_id(assessment.getAssessment_id());
                    Long totalQuestions = questionRepository.countByAssessmentId(assessment.getAssessment_id());
                    return examMapper.toResponseWithoutSections(assessment, totalSections, totalQuestions);
                })
                .collect(Collectors.toList());
        return PageResponse.of(content, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<ExamResponse> getActiveExams() {
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

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ExamResponse> getActiveExams(PageRequest pageRequest) {
        var pageable = pageRequest.toPageable();
        var page = pageRequest.getSearch() != null && !pageRequest.getSearch().trim().isEmpty()
                ? assessmentRepository.findPublishedExamsWithScheduleAndNameContaining(pageRequest.getSearch().trim(), pageable)
                : assessmentRepository.findPublishedExamsWithSchedule(pageable);
        var filteredContent = page.getContent().stream()
                .filter(examValidationService::isExamActive)
                .map(assessment -> {
                    Long totalSections = sectionRepository.countByAssessment_Assessment_id(assessment.getAssessment_id());
                    Long totalQuestions = questionRepository.countByAssessmentId(assessment.getAssessment_id());
                    return examMapper.toResponseWithoutSections(assessment, totalSections, totalQuestions);
                })
                .collect(Collectors.toList());
        
        if (filteredContent.isEmpty() && page.getTotalElements() == 0) {
            throw new ResourceNotFoundException("No active exams found");
        }
        
        return PageResponse.of(filteredContent, pageable, page.getTotalElements());
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
        Assessment assessment = assessmentRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam schedule", examId.toString()));
        
        return Optional.of(examMapper.toScheduleResponse(assessment));
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
        LocalDateTime now = LocalDateTime.now(CAMBODIA_ZONE);

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

    @Override
    @Transactional(readOnly = true)
    public ExamResponse getActiveExamForStudent(UUID examId) {
        UUID studentId = SecurityUtils.getCurrentUserId();
        
        if (studentId == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        Assessment assessment = assessmentRepository.findByIdWithSections(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", examId.toString()));

        if (!examValidationService.isExamActive(assessment)) {
            throw new IllegalStateException("Exam is not currently active");
        }

        if (!studentAssessmentRepository.existsByStudentIdAndAssessmentId(studentId, examId)) {
            throw new IllegalStateException("Assessment is not assigned to this student");
        }

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
    }
}
