package com.kshrd.assessment.controller;

import com.kshrd.assessment.dto.exam.QuestionResponse;
import com.kshrd.assessment.dto.exam.QuestionUpdateRequest;
import com.kshrd.assessment.service.IExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v4/questions")
@AllArgsConstructor
@Tag(name = "Question Management", description = "APIs for managing questions within sections")
@SecurityRequirement(name = "bearerAuth")
public class QuestionController {

    private final IExamService examService;

    @GetMapping("/{questionId}")
    @PreAuthorize("hasRole('student') or hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Get question by ID", description = "Retrieves a specific question by its unique identifier including question type, content, image, and associated section ID")
    public ResponseEntity<QuestionResponse> getQuestion(@PathVariable UUID questionId) {
        Optional<QuestionResponse> response = examService.getQuestionById(questionId);
        return response.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{questionId}")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Update question", description = "Updates the details of an existing question including type, image, and question content")
    public ResponseEntity<Void> updateQuestion(
            @PathVariable UUID questionId,
            @RequestBody @Valid QuestionUpdateRequest request) {
        examService.updateQuestion(questionId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{questionId}")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Delete question", description = "Deletes a question from a section")
    public ResponseEntity<Void> deleteQuestion(@PathVariable UUID questionId) {
        examService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }
}
