package com.kshrd.assessment.controller;

import com.kshrd.assessment.dto.exam.ExamRequest;
import com.kshrd.assessment.dto.exam.ExamResponse;
import com.kshrd.assessment.dto.exam.ExamScheduleRequest;
import com.kshrd.assessment.dto.exam.ExamScheduleResponse;
import com.kshrd.assessment.service.IExamService;
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
@RequestMapping("/api/v4/exams")
@AllArgsConstructor
@Tag(name = "Exam Management", description = "APIs for managing exams/assessments")
@SecurityRequirement(name = "bearerAuth")
public class ExamController {

    private final IExamService examService;

    @PostMapping
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Create a new exam", description = "Creates a new exam/assessment with optional sections and questions. All sections and questions can be included in the request body.")
    public ResponseEntity<ExamResponse> createExam(@RequestBody @Valid ExamRequest request) {
        ExamResponse response = examService.createExam(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{examId}")
    @PreAuthorize("hasRole('student') or hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Get exam by ID", description = "Retrieves a specific exam/assessment by its unique identifier")
    public ResponseEntity<ExamResponse> getExam(@PathVariable UUID examId) {
        Optional<ExamResponse> response = examService.getExamById(examId);
        return response.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('student') or hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Get all exams", description = "Retrieves a list of all exams/assessments in the system")
    public ResponseEntity<List<ExamResponse>> getAllExams() {
        List<ExamResponse> responses = examService.getAllExams();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{examId}/schedule")
    @PreAuthorize("hasRole('student') or hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Get exam schedule", description = "Retrieves the schedule information for a specific exam including assessment date, start time, and end time")
    public ResponseEntity<ExamScheduleResponse> getExamSchedule(@PathVariable UUID examId) {
        Optional<ExamScheduleResponse> response = examService.getExamSchedule(examId);
        return response.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{examId}")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Update exam", description = "Updates the details of an existing exam/assessment including name, quiz status, subject, and schedule")
    public ResponseEntity<ExamResponse> updateExam(
            @PathVariable UUID examId,
            @RequestBody @Valid ExamRequest request) {
        ExamResponse response = examService.updateExam(examId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{examId}/schedule")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Update exam schedule", description = "Updates the schedule (date, start time, end time) of an exam. Can only be updated before the exam start date/time")
    public ResponseEntity<ExamScheduleResponse> updateSchedule(
            @PathVariable UUID examId,
            @RequestBody @Valid ExamScheduleRequest request) {
        ExamScheduleResponse response = examService.updateSchedule(examId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{examId}/publish")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Publish exam", description = "Publishes an exam making it available to students. Can only be published before the exam start date")
    public ResponseEntity<ExamResponse> publishExam(@PathVariable UUID examId) {
        ExamResponse response = examService.publishExam(examId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{examId}/unpublish")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Unpublish exam", description = "Unpublishes an exam, making it unavailable to students")
    public ResponseEntity<ExamResponse> unpublishExam(@PathVariable UUID examId) {
        ExamResponse response = examService.unpublishExam(examId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{examId}/clone")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Clone exam", description = "Creates a copy of an existing exam including all its sections and questions. The cloned exam will have '(Copy)' appended to its name and will be unpublished")
    public ResponseEntity<ExamResponse> cloneExam(@PathVariable UUID examId) {
        ExamResponse response = examService.cloneExam(examId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{examId}")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Delete exam", description = "Deletes an exam/assessment and all its associated sections and questions")
    public ResponseEntity<Void> deleteExam(@PathVariable UUID examId) {
        examService.deleteExam(examId);
        return ResponseEntity.noContent().build();
    }
}
