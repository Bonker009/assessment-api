package com.kshrd.assessment.controller;

import com.kshrd.assessment.entity.Answer;
import com.kshrd.assessment.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v4/answers")
@AllArgsConstructor
@Tag(name = "Answer Management", description = "APIs for managing student answers")
@SecurityRequirement(name = "bearerAuth")
public class AnswerController {

    @PostMapping
    @PreAuthorize("hasRole('student')")
    @Operation(summary = "Create answer", description = "Creates an answer for a question. Student ID is automatically extracted from JWT token")
    public ResponseEntity<Answer> createAnswer(@RequestBody Answer answer) {
        UUID studentId = SecurityUtils.getCurrentUserId();
        
        if (studentId == null) {
            return ResponseEntity.badRequest().build();
        }

        answer.setStudent_id(studentId);

        return ResponseEntity.ok(answer);
    }



    @GetMapping("/me")
    @PreAuthorize("hasRole('student')")
    @Operation(summary = "Get current user info", description = "Retrieves the current authenticated user's information from JWT token")
    public ResponseEntity<UserInfo> getCurrentUserInfo() {
        UUID userId = SecurityUtils.getCurrentUserId();
        String username = SecurityUtils.getCurrentUsername();
        
        return ResponseEntity.ok(new UserInfo(userId, username));
    }
    public record UserInfo(UUID userId, String username) {}
}
