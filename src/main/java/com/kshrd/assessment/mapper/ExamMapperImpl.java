package com.kshrd.assessment.mapper;

import com.kshrd.assessment.dto.exam.ExamResponse;
import com.kshrd.assessment.dto.exam.ExamScheduleResponse;
import com.kshrd.assessment.dto.exam.QuestionResponse;
import com.kshrd.assessment.dto.exam.SectionResponse;
import com.kshrd.assessment.entity.Assessment;
import com.kshrd.assessment.entity.Question;
import com.kshrd.assessment.entity.Section;
import org.springframework.stereotype.Component;

@Component
public class ExamMapperImpl implements IExamMapper {

    public ExamResponse toResponse(Assessment entity) {
        if (entity == null) {
            return null;
        }

        return new ExamResponse(
                entity.getAssessment_id(),
                entity.getName(),
                entity.getIsQuiz(),
                entity.getSubjectId(),
                entity.getAssessmentDate(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getIsPublished(),
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
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

        return new SectionResponse(
                entity.getSection_id(),
                entity.getSectionName(),
                entity.getAssessment() != null ? entity.getAssessment().getAssessment_id() : null
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
                entity.getSection() != null ? entity.getSection().getSection_id() : null
        );
    }
}
