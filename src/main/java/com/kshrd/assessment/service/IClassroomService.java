package com.kshrd.assessment.service;

import com.kshrd.assessment.dto.classroom.AssignAssessmentToClassroomRequest;
import com.kshrd.assessment.dto.classroom.ClassroomRequest;
import com.kshrd.assessment.dto.classroom.ClassroomResponse;
import com.kshrd.assessment.dto.response.PageRequest;
import com.kshrd.assessment.dto.response.PageResponse;

import java.util.List;
import java.util.UUID;

public interface IClassroomService {
    ClassroomResponse createClassroom(ClassroomRequest request);
    ClassroomResponse updateClassroom(UUID classroomId, ClassroomRequest request);
    void deleteClassroom(UUID classroomId);
    ClassroomResponse getClassroomById(UUID classroomId);
    List<ClassroomResponse> getMyClassrooms();
    PageResponse<ClassroomResponse> getMyClassrooms(PageRequest pageRequest);
    List<ClassroomResponse> getAllClassrooms();
    PageResponse<ClassroomResponse> getAllClassrooms(PageRequest pageRequest);
    ClassroomResponse assignAssessmentsToClassroom(UUID classroomId, AssignAssessmentToClassroomRequest request);
    ClassroomResponse removeAssessmentsFromClassroom(UUID classroomId, AssignAssessmentToClassroomRequest request);
    List<ClassroomResponse> getClassroomsBySubjectId(UUID subjectId);
}

