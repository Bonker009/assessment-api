package com.kshrd.assessment.mapper;

import com.kshrd.assessment.dto.studentassessment.StudentAssessmentResponse;
import com.kshrd.assessment.entity.StudentAssessment;
import org.springframework.stereotype.Component;

@Component
public class StudentAssessmentMapperImpl implements IStudentAssessmentMapper {

    public StudentAssessmentResponse toResponse(StudentAssessment entity) {
        if (entity == null) {
            return null;
        }

        return new StudentAssessmentResponse(
                entity.getStudentId(),
                entity.getAssessmentId(),
                entity.getScore(),
                entity.getStatus(),
                entity.getJoinAt(),
                entity.getDurationInMinute(),
                entity.getSubmittedAt(),
                entity.getGradingStatus(),
                entity.getTotalScore()
        );
    }

    public StudentAssessmentResponse toResponseOrNull(StudentAssessment entity) {
        return entity != null ? toResponse(entity) : null;
    }
}
