package com.kshrd.assessment.controller;

import com.kshrd.assessment.dto.answer.SubmitAnswersRequest;
import com.kshrd.assessment.dto.response.ApiResponse;
import com.kshrd.assessment.dto.response.ResponseUtil;
import com.kshrd.assessment.dto.studentassessment.GradeAssessmentRequest;
import com.kshrd.assessment.dto.studentassessment.StudentAssessmentRequest;
import com.kshrd.assessment.dto.studentassessment.StudentAssessmentResponse;
import com.kshrd.assessment.dto.studentassessment.SubmitAssessmentRequest;
import com.kshrd.assessment.service.IStudentAssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v4/student-assessments")
@AllArgsConstructor
@Tag(name = "Student Assessment Management", description = "APIs for managing student assessments")
@SecurityRequirement(name = "bearerAuth")
public class StudentAssessmentController {

    private final IStudentAssessmentService studentAssessmentService;

    @PostMapping("/assign")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Assign assessment to student", description = "Assigns an assessment to a student. Only accessible by teachers and admins")
    public ResponseEntity<ApiResponse<StudentAssessmentResponse>> assignAssessment(
            @RequestBody @Valid StudentAssessmentRequest request) {
        StudentAssessmentResponse response = studentAssessmentService.assignAssessment(request);
        return ResponseUtil.created(response, "Assessment assigned successfully");
    }

    @PostMapping("/{assessmentId}/start")
    @PreAuthorize("hasRole('student')")
    @Operation(summary = "Start assessment", description = "Starts an assessment when a student begins taking it")
    public ResponseEntity<ApiResponse<StudentAssessmentResponse>> startAssessment(
            @PathVariable UUID assessmentId) {
        StudentAssessmentResponse response = studentAssessmentService.startAssessment(assessmentId);
        return ResponseUtil.ok(response, "Assessment started successfully");
    }

    @PostMapping("/submit")
    @PreAuthorize("hasRole('student')")
    @Operation(summary = "Submit assessment", description = "Submits a completed assessment with score and duration")
    public ResponseEntity<ApiResponse<StudentAssessmentResponse>> submitAssessment(
            @RequestBody @Valid SubmitAssessmentRequest request) {
        StudentAssessmentResponse response = studentAssessmentService.submitAssessment(request);
        return ResponseUtil.ok(response, "Assessment submitted successfully");
    }

    @PostMapping("/submit-with-answers")
    @PreAuthorize("hasRole('student')")
    @Operation(summary = "Submit assessment with answers", description = "Submits a completed assessment along with all question answers")
    public ResponseEntity<ApiResponse<StudentAssessmentResponse>> submitAssessmentWithAnswers(
            @RequestBody @Valid SubmitAnswersRequest request) {
        StudentAssessmentResponse response = studentAssessmentService.submitAssessmentWithAnswers(request);
        return ResponseUtil.ok(response, "Assessment and answers submitted successfully");
    }

    @GetMapping("/my-assessments")
    @PreAuthorize("hasRole('student')")
    @Operation(summary = "Get my assessments", description = "Retrieves all assessments assigned to the currently authenticated student")
    public ResponseEntity<ApiResponse<List<StudentAssessmentResponse>>> getMyAssessments() {
        List<StudentAssessmentResponse> responses = studentAssessmentService.getMyAssessments();
        return ResponseUtil.ok(responses, "Assessments retrieved successfully");
    }

    @GetMapping("/{assessmentId}")
    @PreAuthorize("hasRole('student')")
    @Operation(summary = "Get my assessment", description = "Retrieves a specific assessment for the currently authenticated student")
    public ResponseEntity<ApiResponse<StudentAssessmentResponse>> getMyAssessment(
            @PathVariable UUID assessmentId) {
        Optional<StudentAssessmentResponse> response = studentAssessmentService.getMyAssessment(assessmentId);
        return response.map(ResponseUtil::ok)
                .orElse(ResponseUtil.notFound("Assessment not found"));
    }

    @PostMapping("/grade")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Grade assessment", description = "Grades a submitted assessment. Only accessible by teachers and admins")
    public ResponseEntity<ApiResponse<StudentAssessmentResponse>> gradeAssessment(
            @RequestBody @Valid GradeAssessmentRequest request) {
        StudentAssessmentResponse response = studentAssessmentService.gradeAssessment(request);
        return ResponseUtil.ok(response, "Assessment graded successfully");
    }
}
