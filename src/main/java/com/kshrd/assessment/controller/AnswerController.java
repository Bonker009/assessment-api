package com.kshrd.assessment.controller;

import com.kshrd.assessment.aop.annotation.AuditSecurity;
import com.kshrd.assessment.aop.annotation.LogError;
import com.kshrd.assessment.aop.annotation.LogExecution;
import com.kshrd.assessment.aop.annotation.LogPerformance;
import com.kshrd.assessment.dto.answer.AnswerRequest;
import com.kshrd.assessment.dto.answer.AnswerResponse;
import com.kshrd.assessment.dto.answer.SubmitAllAnswersRequest;
import com.kshrd.assessment.dto.answer.SubmitSingleAnswerRequest;
import com.kshrd.assessment.dto.response.ApiResponse;
import com.kshrd.assessment.dto.response.PageRequest;
import com.kshrd.assessment.dto.response.PageResponse;
import com.kshrd.assessment.dto.response.ResponseUtil;
import com.kshrd.assessment.service.IAnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v4/answers")
@AllArgsConstructor
@Tag(name = "Answer Management", description = "APIs for managing student answers")
@SecurityRequirement(name = "bearerAuth")
@LogExecution(logParameters = true, logReturnValue = false, description = "Answer Controller")
@LogPerformance(thresholdMillis = 500, description = "Answer Controller Performance")
@LogError(logStackTrace = true, description = "Answer Controller Error Handling")
@AuditSecurity(action = "Answer Management", resource = "Answer", logParameters = true)
public class AnswerController {

    private final IAnswerService answerService;

    @PostMapping
    @PreAuthorize("hasRole('student')")
    @Operation(summary = "Create or update answer", description = "Creates or updates an answer for a question. Student ID is automatically extracted from JWT token. Score is optional.")
    public ResponseEntity<ApiResponse<AnswerResponse>> createAnswer(@RequestBody @Valid AnswerRequest request) {
        AnswerResponse response = answerService.saveOrUpdateAnswer(request);
        return ResponseUtil.created(response, "Answer saved successfully");
    }

    @PostMapping("/submit-single")
    @PreAuthorize("hasRole('student')")
    @Operation(summary = "Submit single answer", description = "Submits a single answer for a question without score. This is for piece-by-piece submission during the assessment.")
    public ResponseEntity<ApiResponse<AnswerResponse>> submitSingleAnswer(@RequestBody @Valid SubmitSingleAnswerRequest request) {
        AnswerResponse response = answerService.submitSingleAnswer(request);
        return ResponseUtil.ok(response, "Answer submitted successfully");
    }

    @PostMapping("/submit-all")
    @PreAuthorize("hasRole('student')")
    @Operation(summary = "Submit all answers at once", description = "Submits all answers for an assessment at once without score. This is for bulk submission of all answers.")
    public ResponseEntity<ApiResponse<List<AnswerResponse>>> submitAllAnswers(@RequestBody @Valid SubmitAllAnswersRequest request) {
        List<AnswerResponse> responses = answerService.submitAllAnswers(request);
        return ResponseUtil.ok(responses, "All answers submitted successfully");
    }

    @PutMapping("/{answerId}")
    @PreAuthorize("hasRole('student')")
    @Operation(summary = "Update answer", description = "Updates an existing answer by ID")
    public ResponseEntity<ApiResponse<AnswerResponse>> updateAnswer(
            @PathVariable UUID answerId,
            @RequestBody @Valid AnswerRequest request) {
        AnswerResponse response = answerService.updateAnswer(answerId, request);
        return ResponseUtil.ok(response, "Answer updated successfully");
    }

    @GetMapping("/assessment/{assessmentId}")
    @PreAuthorize("hasRole('student')")
    @Operation(summary = "Get my answers for assessment", description = "Retrieves all answers for the current student in a specific assessment with pagination support")
    public ResponseEntity<ApiResponse<PageResponse<AnswerResponse>>> getMyAnswersByAssessment(
            @PathVariable UUID assessmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String search) {
        PageRequest pageRequest = new PageRequest(page, size, sortBy, sortDirection, search);
        PageResponse<AnswerResponse> responses = answerService.getMyAnswersByAssessment(assessmentId, pageRequest);
        return ResponseUtil.ok(responses, "Answers retrieved successfully");
    }

    @GetMapping("/question/{questionId}")
    @PreAuthorize("hasRole('student')")
    @Operation(summary = "Get answer for question", description = "Retrieves the answer for a specific question")
    public ResponseEntity<ApiResponse<AnswerResponse>> getAnswerByQuestion(
            @PathVariable UUID questionId) {
        AnswerResponse response = answerService.getAnswerByQuestion(questionId);
        return ResponseUtil.ok(response, "Answer retrieved successfully");
    }

    @GetMapping("/assessment/{assessmentId}/all")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Get all answers for assessment", description = "Retrieves all answers for a specific assessment with pagination support. Only accessible by teachers and admins")
    public ResponseEntity<ApiResponse<PageResponse<AnswerResponse>>> getAllAnswersByAssessment(
            @PathVariable UUID assessmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String search) {
        PageRequest pageRequest = new PageRequest(page, size, sortBy, sortDirection, search);
        PageResponse<AnswerResponse> responses = answerService.getAnswersByAssessment(assessmentId, pageRequest);
        return ResponseUtil.ok(responses, "Answers retrieved successfully");
    }
}
