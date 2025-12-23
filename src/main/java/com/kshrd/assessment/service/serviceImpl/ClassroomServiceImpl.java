package com.kshrd.assessment.service.serviceImpl;

import com.kshrd.assessment.aop.annotation.AuditSecurity;
import com.kshrd.assessment.aop.annotation.LogError;
import com.kshrd.assessment.aop.annotation.LogExecution;
import com.kshrd.assessment.aop.annotation.LogPerformance;
import com.kshrd.assessment.dto.classroom.AssignAssessmentToClassroomRequest;
import com.kshrd.assessment.dto.classroom.ClassroomRequest;
import com.kshrd.assessment.dto.classroom.ClassroomResponse;
import com.kshrd.assessment.dto.response.PageRequest;
import com.kshrd.assessment.dto.response.PageResponse;
import com.kshrd.assessment.entity.Classroom;
import com.kshrd.assessment.entity.ClassroomAssessment;
import com.kshrd.assessment.exception.ResourceNotFoundException;
import com.kshrd.assessment.repository.AssessmentRepository;
import com.kshrd.assessment.repository.ClassroomAssessmentRepository;
import com.kshrd.assessment.repository.ClassroomRepository;
import com.kshrd.assessment.service.IClassroomService;
import com.kshrd.assessment.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@LogExecution(logParameters = true, logReturnValue = false, description = "Classroom Service")
@LogPerformance(thresholdMillis = 1000, description = "Classroom Service Performance")
@LogError(logStackTrace = true, description = "Classroom Service Error Handling")
@AuditSecurity(action = "Classroom Management", resource = "Classroom", logParameters = true)
public class ClassroomServiceImpl implements IClassroomService {

    private final ClassroomRepository classroomRepository;
    private final AssessmentRepository assessmentRepository;
    private final ClassroomAssessmentRepository classroomAssessmentRepository;

    @Override
    @Transactional
    public ClassroomResponse createClassroom(ClassroomRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        
        if (currentUserId == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        if (!currentUserId.equals(request.teacherId())) {
            throw new IllegalStateException("You can only create classrooms for yourself");
        }

        if (classroomRepository.existsByClassNameAndTeacherId(request.className(), request.teacherId())) {
            throw new IllegalStateException("Classroom with name '" + request.className() + "' already exists for this teacher");
        }

        Classroom classroom = new Classroom();
        classroom.setClassName(request.className().trim());
        classroom.setDescription(request.description());
        classroom.setSubjectId(request.subjectId());
        classroom.setTeacherId(request.teacherId());

        Classroom saved = classroomRepository.save(classroom);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public ClassroomResponse updateClassroom(UUID classroomId, ClassroomRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        
        if (currentUserId == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        Classroom classroom = classroomRepository.findByClassroomIdAndTeacherId(classroomId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", classroomId.toString()));

        if (!request.teacherId().equals(currentUserId)) {
            throw new IllegalStateException("You can only update your own classrooms");
        }

        if (!classroom.getClassName().equals(request.className().trim()) &&
            classroomRepository.existsByClassNameAndTeacherId(request.className(), request.teacherId())) {
            throw new IllegalStateException("Classroom with name '" + request.className() + "' already exists for this teacher");
        }

        classroom.setClassName(request.className().trim());
        classroom.setDescription(request.description());
        classroom.setSubjectId(request.subjectId());

        Classroom saved = classroomRepository.save(classroom);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteClassroom(UUID classroomId) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        
        if (currentUserId == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        Classroom classroom = classroomRepository.findByClassroomIdAndTeacherId(classroomId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", classroomId.toString()));

        classroomRepository.delete(classroom);
    }

    @Override
    @Transactional(readOnly = true)
    public ClassroomResponse getClassroomById(UUID classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", classroomId.toString()));
        return toResponse(classroom);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassroomResponse> getMyClassrooms() {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        
        if (currentUserId == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        List<Classroom> classrooms = classroomRepository.findByTeacherId(currentUserId);
        
        if (classrooms.isEmpty()) {
            throw new ResourceNotFoundException("No classrooms found for current user", currentUserId.toString());
        }

        return classrooms.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ClassroomResponse> getMyClassrooms(PageRequest pageRequest) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        
        if (currentUserId == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        var pageable = pageRequest.toPageable();
        var page = pageRequest.getSearch() != null && !pageRequest.getSearch().trim().isEmpty()
                ? classroomRepository.findByTeacherIdAndClassNameContainingIgnoreCase(
                        currentUserId, pageRequest.getSearch().trim(), pageable)
                : classroomRepository.findByTeacherId(currentUserId, pageable);

        if (page.getContent().isEmpty() && page.getTotalElements() == 0) {
            throw new ResourceNotFoundException("No classrooms found for current user", currentUserId.toString());
        }

        var content = page.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return PageResponse.of(new PageImpl<>(content, pageable, page.getTotalElements()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassroomResponse> getAllClassrooms() {
        List<Classroom> classrooms = classroomRepository.findAll();
        
        if (classrooms.isEmpty()) {
            throw new ResourceNotFoundException("No classrooms found");
        }

        return classrooms.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ClassroomResponse> getAllClassrooms(PageRequest pageRequest) {
        var pageable = pageRequest.toPageable();
        var page = pageRequest.getSearch() != null && !pageRequest.getSearch().trim().isEmpty()
                ? classroomRepository.findByClassNameContainingIgnoreCase(pageRequest.getSearch().trim(), pageable)
                : classroomRepository.findAll(pageable);

        if (page.getContent().isEmpty() && page.getTotalElements() == 0) {
            throw new ResourceNotFoundException("No classrooms found");
        }

        var content = page.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return PageResponse.of(new PageImpl<>(content, pageable, page.getTotalElements()));
    }

    @Override
    @Transactional
    public ClassroomResponse assignAssessmentsToClassroom(UUID classroomId, AssignAssessmentToClassroomRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        
        if (currentUserId == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        Classroom classroom = classroomRepository.findByClassroomIdAndTeacherId(classroomId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", classroomId.toString()));

        for (UUID assessmentId : request.assessmentIds()) {
            if (!assessmentRepository.existsById(assessmentId)) {
                throw new ResourceNotFoundException("Assessment", assessmentId.toString());
            }

            if (!classroomAssessmentRepository.existsByAssessmentIdAndClassroomId(assessmentId, classroomId)) {
                ClassroomAssessment classroomAssessment = new ClassroomAssessment();
                classroomAssessment.setAssessmentId(assessmentId);
                classroomAssessment.setClassroomId(classroomId);
                classroomAssessmentRepository.save(classroomAssessment);
            }
        }

        return toResponse(classroom);
    }

    @Override
    @Transactional
    public ClassroomResponse removeAssessmentsFromClassroom(UUID classroomId, AssignAssessmentToClassroomRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        
        if (currentUserId == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        Classroom classroom = classroomRepository.findByClassroomIdAndTeacherId(classroomId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", classroomId.toString()));

        for (UUID assessmentId : request.assessmentIds()) {
            classroomAssessmentRepository.deleteByAssessmentIdAndClassroomId(assessmentId, classroomId);
        }

        return toResponse(classroom);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassroomResponse> getClassroomsBySubjectId(UUID subjectId) {
        List<Classroom> classrooms = classroomRepository.findBySubjectId(subjectId);
        
        if (classrooms.isEmpty()) {
            throw new ResourceNotFoundException("No classrooms found for subject", subjectId.toString());
        }

        return classrooms.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ClassroomResponse toResponse(Classroom classroom) {
        List<UUID> assessmentIds = classroomAssessmentRepository.findAssessmentIdsByClassroomId(classroom.getClassroomId());

        return new ClassroomResponse(
                classroom.getClassroomId(),
                classroom.getClassName(),
                classroom.getDescription(),
                classroom.getSubjectId(),
                classroom.getTeacherId(),
                assessmentIds,
                classroom.getCreatedBy(),
                classroom.getUpdatedBy(),
                classroom.getCreatedAt(),
                classroom.getUpdatedAt()
        );
    }
}

