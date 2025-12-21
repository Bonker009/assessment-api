package com.kshrd.assessment.mapper;

import com.kshrd.assessment.dto.exam.ExamResponse;
import com.kshrd.assessment.dto.exam.ExamScheduleResponse;
import com.kshrd.assessment.dto.exam.QuestionResponse;
import com.kshrd.assessment.dto.exam.Schedule;
import com.kshrd.assessment.dto.exam.SectionResponse;
import com.kshrd.assessment.entity.Assessment;
import com.kshrd.assessment.entity.Question;
import com.kshrd.assessment.entity.Section;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExamMapperImpl implements IExamMapper {

    public ExamResponse toResponse(Assessment entity) {
        if (entity == null) {
            return null;
        }

        Schedule schedule = new Schedule(
                entity.getAssessmentDate(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getIsPublished()
        );

        Long totalSections = 0L;
        Long totalQuestions = 0L;
        
        if (entity.getSections() != null) {
            totalSections = (long) entity.getSections().size();
            totalQuestions = entity.getSections().stream()
                    .mapToLong(section -> section.getQuestions() != null ? section.getQuestions().size() : 0L)
                    .sum();
        }

        List<SectionResponse> sections = null;
        if (entity.getSections() != null) {
            sections = entity.getSections().stream()
                    .map(this::toSectionResponse)
                    .collect(Collectors.toList());
        }

        return new ExamResponse(
                entity.getAssessment_id(),
                entity.getName(),
                entity.getIsQuiz(),
                entity.getSubjectId(),
                schedule,
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                totalSections,
                totalQuestions,
                sections
        );
    }

    public ExamResponse toResponseWithoutSections(Assessment entity, Long totalSections, Long totalQuestions) {
        if (entity == null) {
            return null;
        }

        Schedule schedule = new Schedule(
                entity.getAssessmentDate(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getIsPublished()
        );

        return new ExamResponse(
                entity.getAssessment_id(),
                entity.getName(),
                entity.getIsQuiz(),
                entity.getSubjectId(),
                schedule,
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                totalSections != null ? totalSections : 0L,
                totalQuestions != null ? totalQuestions : 0L,
                null
        );
    }

    public ExamScheduleResponse toScheduleResponse(Assessment entity) {
        if (entity == null) {
            return null;
        }

        return new ExamScheduleResponse(
                entity.getAssessmentDate(),
                entity.getStartTime(),
                entity.getEndTime()
        );
    }

    public SectionResponse toSectionResponse(Section entity) {
        if (entity == null) {
            return null;
        }

        List<QuestionResponse> questions = null;
        if (entity.getQuestions() != null) {
            questions = entity.getQuestions().stream()
                    .map(this::toQuestionResponse)
                    .collect(Collectors.toList());
        }

        return new SectionResponse(
                entity.getSection_id(),
                entity.getSectionName(),
                entity.getAssessment() != null ? entity.getAssessment().getAssessment_id() : null,
                questions
        );
    }

    public QuestionResponse toQuestionResponse(Question entity) {
        if (entity == null) {
            return null;
        }

        return new QuestionResponse(
                entity.getQuestion_id(),
                entity.getQuestionType(),
                entity.getImage(),
                entity.getQuestionContent(),
                entity.getSection() != null ? entity.getSection().getSection_id() : null,
                entity.getPoints()
        );
    }
}
