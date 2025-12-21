package com.kshrd.assessment.controller;

import com.kshrd.assessment.dto.response.ApiResponse;
import com.kshrd.assessment.dto.response.ResponseUtil;
import com.kshrd.assessment.dto.teacher.TeacherResponse;
import com.kshrd.assessment.service.IKeycloakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v4/teachers")
@AllArgsConstructor
@Tag(name = "Teacher Management", description = "APIs for managing teachers")
@SecurityRequirement(name = "bearerAuth")

public class TeacherController {


    private final IKeycloakService keycloakService;

    @GetMapping
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Get all teachers", description = "Retrieves all teachers")
    public ResponseEntity<ApiResponse<List<TeacherResponse>>> getMyAssessments() {
        List<TeacherResponse> responses = keycloakService.getAllTeachers();
        return ResponseUtil.ok(responses, "Teachers retrieved successfully");
    }


}
