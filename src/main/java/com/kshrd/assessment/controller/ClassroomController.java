package com.kshrd.assessment.controller;

import com.kshrd.assessment.aop.annotation.AuditSecurity;
import com.kshrd.assessment.aop.annotation.LogError;
import com.kshrd.assessment.aop.annotation.LogExecution;
import com.kshrd.assessment.aop.annotation.LogPerformance;
import com.kshrd.assessment.dto.classroom.AssignAssessmentToClassroomRequest;
import com.kshrd.assessment.dto.classroom.ClassroomRequest;
import com.kshrd.assessment.dto.classroom.ClassroomResponse;
import com.kshrd.assessment.dto.response.ApiResponse;
import com.kshrd.assessment.dto.response.PageRequest;
import com.kshrd.assessment.dto.response.PageResponse;
import com.kshrd.assessment.dto.response.ResponseUtil;
import com.kshrd.assessment.service.IClassroomService;
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
@RequestMapping("/api/v4/classrooms")
@AllArgsConstructor
@Tag(name = "Classroom Management", description = "APIs for managing classrooms and assigning assessments")
@SecurityRequirement(name = "bearerAuth")
@LogExecution(logParameters = true, logReturnValue = false, description = "Classroom Controller")
@LogPerformance(thresholdMillis = 500, description = "Classroom Controller Performance")
@LogError(logStackTrace = true, description = "Classroom Controller Error Handling")
@AuditSecurity(action = "Classroom Management", resource = "Classroom", logParameters = true)
public class ClassroomController {

    private final IClassroomService classroomService;

    @PostMapping
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Create classroom", description = "Creates a new classroom. Only accessible by teachers and admins.")
    public ResponseEntity<ApiResponse<ClassroomResponse>> createClassroom(@RequestBody @Valid ClassroomRequest request) {
        ClassroomResponse response = classroomService.createClassroom(request);
        return ResponseUtil.created(response, "Classroom created successfully");
    }

    @PutMapping("/{classroomId}")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Update classroom", description = "Updates an existing classroom. Only accessible by teachers and admins.")
    public ResponseEntity<ApiResponse<ClassroomResponse>> updateClassroom(
            @PathVariable UUID classroomId,
            @RequestBody @Valid ClassroomRequest request) {
        ClassroomResponse response = classroomService.updateClassroom(classroomId, request);
        return ResponseUtil.ok(response, "Classroom updated successfully");
    }

    @DeleteMapping("/{classroomId}")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Delete classroom", description = "Deletes a classroom. Only accessible by teachers and admins.")
    public ResponseEntity<ApiResponse<Void>> deleteClassroom(@PathVariable UUID classroomId) {
        classroomService.deleteClassroom(classroomId);
        return ResponseUtil.noContent("Classroom deleted successfully");
    }

    @GetMapping("/{classroomId}")
    @PreAuthorize("hasRole('teacher') or hasRole('admin') or hasRole('student')")
    @Operation(summary = "Get classroom by ID", description = "Retrieves a specific classroom by its ID")
    public ResponseEntity<ApiResponse<ClassroomResponse>> getClassroomById(@PathVariable UUID classroomId) {
        ClassroomResponse response = classroomService.getClassroomById(classroomId);
        return ResponseUtil.ok(response, "Classroom retrieved successfully");
    }

    @GetMapping("/my-classrooms")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Get my classrooms", description = "Retrieves all classrooms created by the current teacher with pagination and search support")
    public ResponseEntity<ApiResponse<PageResponse<ClassroomResponse>>> getMyClassrooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String search) {
        PageRequest pageRequest = new PageRequest(page, size, sortBy, sortDirection, search);
        PageResponse<ClassroomResponse> response = classroomService.getMyClassrooms(pageRequest);
        return ResponseUtil.ok(response, "Classrooms retrieved successfully");
    }

    @GetMapping
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Get all classrooms", description = "Retrieves all classrooms in the system with pagination and search support")
    public ResponseEntity<ApiResponse<PageResponse<ClassroomResponse>>> getAllClassrooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String search) {
        PageRequest pageRequest = new PageRequest(page, size, sortBy, sortDirection, search);
        PageResponse<ClassroomResponse> response = classroomService.getAllClassrooms(pageRequest);
        return ResponseUtil.ok(response, "Classrooms retrieved successfully");
    }

    @PostMapping("/{classroomId}/assign-assessments")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Assign assessments to classroom", description = "Assigns one or more assessments to a classroom")
    public ResponseEntity<ApiResponse<ClassroomResponse>> assignAssessmentsToClassroom(
            @PathVariable UUID classroomId,
            @RequestBody @Valid AssignAssessmentToClassroomRequest request) {
        ClassroomResponse response = classroomService.assignAssessmentsToClassroom(classroomId, request);
        return ResponseUtil.ok(response, "Assessments assigned to classroom successfully");
    }

    @PostMapping("/{classroomId}/remove-assessments")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Remove assessments from classroom", description = "Removes one or more assessments from a classroom")
    public ResponseEntity<ApiResponse<ClassroomResponse>> removeAssessmentsFromClassroom(
            @PathVariable UUID classroomId,
            @RequestBody @Valid AssignAssessmentToClassroomRequest request) {
        ClassroomResponse response = classroomService.removeAssessmentsFromClassroom(classroomId, request);
        return ResponseUtil.ok(response, "Assessments removed from classroom successfully");
    }

    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasRole('teacher') or hasRole('admin') or hasRole('student')")
    @Operation(summary = "Get classrooms by subject", description = "Retrieves all classrooms for a specific subject")
    public ResponseEntity<ApiResponse<List<ClassroomResponse>>> getClassroomsBySubjectId(@PathVariable UUID subjectId) {
        List<ClassroomResponse> response = classroomService.getClassroomsBySubjectId(subjectId);
        return ResponseUtil.ok(response, "Classrooms retrieved successfully");
    }
}

