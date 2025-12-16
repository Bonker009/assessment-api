package com.kshrd.assessment.controller;

import com.kshrd.assessment.dto.exam.SectionResponse;
import com.kshrd.assessment.dto.exam.SectionUpdateRequest;
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
@RequestMapping("/api/v4/sections")
@AllArgsConstructor
@Tag(name = "Section Management", description = "APIs for managing sections within exams")
@SecurityRequirement(name = "bearerAuth")
public class SectionController {

    private final IExamService examService;

    @GetMapping("/{sectionId}")
    @PreAuthorize("hasRole('student') or hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Get section by ID", description = "Retrieves a specific section by its unique identifier including section name and associated assessment ID")
    public ResponseEntity<SectionResponse> getSection(@PathVariable UUID sectionId) {
        Optional<SectionResponse> response = examService.getSectionById(sectionId);
        return response.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{sectionId}")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Update section", description = "Updates the name of an existing section within an exam")
    public ResponseEntity<Void> updateSection(
            @PathVariable UUID sectionId,
            @RequestBody @Valid SectionUpdateRequest request) {
        examService.updateSection(sectionId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{sectionId}")
    @PreAuthorize("hasRole('teacher') or hasRole('admin')")
    @Operation(summary = "Delete section", description = "Deletes a section and all its associated questions from an exam")
    public ResponseEntity<Void> deleteSection(@PathVariable UUID sectionId) {
        examService.deleteSection(sectionId);
        return ResponseEntity.noContent().build();
    }
}
