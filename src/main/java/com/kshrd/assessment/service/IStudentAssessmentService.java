package com.kshrd.assessment.service;

import com.kshrd.assessment.dto.answer.SubmitAnswersRequest;
import com.kshrd.assessment.dto.studentassessment.GradeAssessmentRequest;
import com.kshrd.assessment.dto.studentassessment.StudentAssessmentRequest;
import com.kshrd.assessment.dto.studentassessment.StudentAssessmentResponse;
import com.kshrd.assessment.dto.studentassessment.SubmitAssessmentRequest;
import com.kshrd.assessment.entity.StudentAssessment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.kshrd.assessment.dto.response.PageRequest;
import com.kshrd.assessment.dto.response.PageResponse;

public interface IStudentAssessmentService {
    StudentAssessmentResponse assignAssessment(StudentAssessmentRequest request);
    StudentAssessmentResponse startAssessment(UUID assessmentId);
    StudentAssessmentResponse submitAssessment(SubmitAssessmentRequest request);
    StudentAssessmentResponse submitAssessmentWithAnswers(SubmitAnswersRequest request);
    List<StudentAssessmentResponse> getMyAssessments();
    PageResponse<StudentAssessmentResponse> getMyAssessments(PageRequest pageRequest);
    Optional<StudentAssessmentResponse> getMyAssessment(UUID assessmentId);
    StudentAssessmentResponse gradeAssessment(GradeAssessmentRequest request);
    Optional<StudentAssessment> findById(UUID studentId, UUID assessmentId);
}
