package com.kshrd.assessment.controller;

import com.kshrd.assessment.dto.answer.AnswerRequest;
import com.kshrd.assessment.dto.answer.AnswerResponse;
import com.kshrd.assessment.dto.response.ApiResponse;
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
public class AnswerController {

    private final IAnswerService answerService;

    @PostMapping
    @PreAuthorize("hasRole('student')")
    @Operation(summary = "Create or update answer", description = "Creates or updates an answer for a question. Student ID is automatically extracted from JWT token")
    public ResponseEntity<ApiResponse<AnswerResponse>> createAnswer(@RequestBody @Valid AnswerRequest request) {
        AnswerResponse response = answerService.saveOrUpdateAnswer(request);
        return ResponseUtil.created(response, "Answer saved successfully");
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
    @Operation(summary = "Get my answers for assessment", description = "Retrieves all answers for the current student in a specific assessment")
    public ResponseEntity<ApiResponse<List<AnswerResponse>>> getMyAnswersByAssessment(
            @PathVariable UUID assessmentId) {
        List<AnswerResponse> responses = answerService.getMyAnswersByAssessment(assessmentId);
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
    @Operation(summary = "Get all answers for assessment", description = "Retrieves all answers for a specific assessment. Only accessible by teachers and admins")
    public ResponseEntity<ApiResponse<List<AnswerResponse>>> getAllAnswersByAssessment(
            @PathVariable UUID assessmentId) {
        List<AnswerResponse> responses = answerService.getAnswersByAssessment(assessmentId);
        return ResponseUtil.ok(responses, "Answers retrieved successfully");
    }
}
