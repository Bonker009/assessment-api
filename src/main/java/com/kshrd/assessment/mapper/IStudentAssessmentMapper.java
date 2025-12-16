package com.kshrd.assessment.mapper;

import com.kshrd.assessment.dto.studentassessment.StudentAssessmentResponse;
import com.kshrd.assessment.entity.StudentAssessment;

public interface IStudentAssessmentMapper {
    StudentAssessmentResponse toResponse(StudentAssessment entity);
    StudentAssessmentResponse toResponseOrNull(StudentAssessment entity);
}
