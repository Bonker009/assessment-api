package com.kshrd.assessment.controller;

import com.kshrd.assessment.dto.exam.ExamRequest;
import com.kshrd.assessment.dto.exam.ExamResponse;
import com.kshrd.assessment.dto.exam.ExamScheduleRequest;
import com.kshrd.assessment.dto.exam.ExamScheduleResponse;
import com.kshrd.assessment.dto.response.ApiResponse;
import com.kshrd.assessment.dto.response.PageRequest;
import com.kshrd.assessment.dto.response.PageResponse;
import com.kshrd.assessment.dto.response.ResponseUtil;
import com.kshrd.assessment.service.IExamService;
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
@RequestMapping("/api/v4/exams")
@AllArgsConstructor
@Tag(name = "Exam Management", description = "APIs for managing exams/assessments")
@SecurityRequirement(name = "bearerAuth")
public class ExamController {

    private final IExamService examService;

    @PostMapping
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Create a new exam", description = "Creates a new exam/assessment with optional sections and questions. All sections and questions can be included in the request body.")
    public ResponseEntity<ApiResponse<ExamResponse>> createExam(@RequestBody @Valid ExamRequest request) {
        ExamResponse response = examService.createExam(request);
        return ResponseUtil.created(response, "Exam created successfully");
    }

    @GetMapping("/{examId}")
    @PreAuthorize("hasRole('student') or hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Get exam by ID", description = "Retrieves a specific exam/assessment by its unique identifier")
    public ResponseEntity<ApiResponse<ExamResponse>> getExam(@PathVariable UUID examId) {
        Optional<ExamResponse> response = examService.getExamById(examId);
        return response.map(ResponseUtil::ok)
                .orElse(ResponseUtil.notFound("Exam not found"));
    }

    @GetMapping("/my-exams")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")

    @Operation(summary = "Get all exams by its owner", description = "Retrieves all exams/assessments by its owner with pagination and search support")
    public ResponseEntity<ApiResponse<PageResponse<ExamResponse>>> getMyExams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String search) {
        PageRequest pageRequest = new PageRequest(page, size, sortBy, sortDirection, search);
        PageResponse<ExamResponse> response = examService.getMyExams(pageRequest);
        return ResponseUtil.ok(response, "Exams retrieved successfully");
    }

    @GetMapping
    @PreAuthorize("hasRole('student') or hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Get all exams", description = "Retrieves a list of all exams/assessments in the system with pagination and search support")
    public ResponseEntity<ApiResponse<PageResponse<ExamResponse>>> getAllExams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String search) {
        PageRequest pageRequest = new PageRequest(page, size, sortBy, sortDirection, search);
        PageResponse<ExamResponse> response = examService.getAllExams(pageRequest);
        return ResponseUtil.ok(response, "Exams retrieved successfully");
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('student') or hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Get active exams", description = "Retrieves a list of currently active exams with pagination and search support. An exam is active if the current time is within the scheduled start and end time on the assessment date.")
    public ResponseEntity<ApiResponse<PageResponse<ExamResponse>>> getActiveExams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String search) {
        PageRequest pageRequest = new PageRequest(page, size, sortBy, sortDirection, search);
        PageResponse<ExamResponse> response = examService.getActiveExams(pageRequest);
        return ResponseUtil.ok(response, "Active exams retrieved successfully");
    }

    @GetMapping("/{examId}/schedule")
    @PreAuthorize("hasRole('student') or hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Get exam schedule", description = "Retrieves the schedule information for a specific exam including assessment date, start time, and end time")
    public ResponseEntity<ApiResponse<ExamScheduleResponse>> getExamSchedule(@PathVariable UUID examId) {
        Optional<ExamScheduleResponse> response = examService.getExamSchedule(examId);
        return response.map(ResponseUtil::ok)
                .orElse(ResponseUtil.notFound("Exam schedule not found"));
    }

    @PutMapping("/{examId}")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Update exam", description = "Updates the details of an existing exam/assessment including name, quiz status, subject, and schedule")
    public ResponseEntity<ApiResponse<ExamResponse>> updateExam(
            @PathVariable UUID examId,
            @RequestBody @Valid ExamRequest request) {
        ExamResponse response = examService.updateExam(examId, request);
        return ResponseUtil.ok(response, "Exam updated successfully");
    }

    @PutMapping("/{examId}/schedule")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Update exam schedule", description = "Updates the schedule (date, start time, end time) of an exam. Can only be updated before the exam start date/time")
    public ResponseEntity<ApiResponse<ExamScheduleResponse>> updateSchedule(
            @PathVariable UUID examId,
            @RequestBody @Valid ExamScheduleRequest request) {
        ExamScheduleResponse response = examService.updateSchedule(examId, request);
        return ResponseUtil.ok(response, "Exam schedule updated successfully");
    }

    @PostMapping("/{examId}/clone")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Clone exam", description = "Creates a copy of an existing exam including all its sections and questions. The cloned exam will have '(Copy)' appended to its name")
    public ResponseEntity<ApiResponse<ExamResponse>> cloneExam(@PathVariable UUID examId) {
        ExamResponse response = examService.cloneExam(examId);
        return ResponseUtil.created(response, "Exam cloned successfully");
    }

    @DeleteMapping("/{examId}")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Delete exam", description = "Deletes an exam/assessment and all its associated sections and questions")
    public ResponseEntity<ApiResponse<Void>> deleteExam(@PathVariable UUID examId) {
        examService.deleteExam(examId);
        return ResponseUtil.noContent("Exam deleted successfully");
    }

    @GetMapping("/{examId}/student-view")
    @PreAuthorize("hasRole('student')")
    @Operation(summary = "Get active exam details for student", description = "Retrieves exam details with questions for an active exam assigned to the student. Returns questions only, no student answers.")
    public ResponseEntity<ApiResponse<ExamResponse>> getActiveExamForStudent(@PathVariable UUID examId) {
        ExamResponse response = examService.getActiveExamForStudent(examId);
        return ResponseUtil.ok(response, "Active exam details retrieved successfully");
    }
}
