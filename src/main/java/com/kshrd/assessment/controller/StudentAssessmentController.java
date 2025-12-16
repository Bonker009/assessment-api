package com.kshrd.assessment.controller;

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
import org.springframework.http.HttpStatus;
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
    @PreAuthorize("hasRole('student')")
    @Operation(summary = "Assign assessment to student", description = "Assigns an assessment to the currently authenticated student")
    public ResponseEntity<StudentAssessmentResponse> assignAssessment(
            @RequestBody @Valid StudentAssessmentRequest request) {
        StudentAssessmentResponse response = studentAssessmentService.assignAssessment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{assessmentId}/start")
    @PreAuthorize("hasRole('student')")
    @Operation(summary = "Start assessment", description = "Starts an assessment when a student begins taking it")
    public ResponseEntity<StudentAssessmentResponse> startAssessment(
            @PathVariable UUID assessmentId) {
        StudentAssessmentResponse response = studentAssessmentService.startAssessment(assessmentId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit")
    @PreAuthorize("hasRole('student')")
    @Operation(summary = "Submit assessment", description = "Submits a completed assessment with score and duration")
    public ResponseEntity<StudentAssessmentResponse> submitAssessment(
            @RequestBody @Valid SubmitAssessmentRequest request) {
        StudentAssessmentResponse response = studentAssessmentService.submitAssessment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-assessments")
    @PreAuthorize("hasRole('student')")
    @Operation(summary = "Get my assessments", description = "Retrieves all assessments assigned to the currently authenticated student")
    public ResponseEntity<List<StudentAssessmentResponse>> getMyAssessments() {
        List<StudentAssessmentResponse> responses = studentAssessmentService.getMyAssessments();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{assessmentId}")
    @PreAuthorize("hasRole('student')")
    @Operation(summary = "Get my assessment", description = "Retrieves a specific assessment for the currently authenticated student")
    public ResponseEntity<StudentAssessmentResponse> getMyAssessment(
            @PathVariable UUID assessmentId) {
        Optional<StudentAssessmentResponse> response = studentAssessmentService.getMyAssessment(assessmentId);
        return response.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/grade")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Grade assessment", description = "Grades a submitted assessment. Only accessible by teachers and admins")
    public ResponseEntity<StudentAssessmentResponse> gradeAssessment(
            @RequestBody @Valid GradeAssessmentRequest request) {
        StudentAssessmentResponse response = studentAssessmentService.gradeAssessment(request);
        return ResponseEntity.ok(response);
    }
}
