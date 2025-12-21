package com.kshrd.assessment.service;

import com.kshrd.assessment.dto.auth.LoginRequest;
import com.kshrd.assessment.dto.auth.LoginResponse;
import com.kshrd.assessment.dto.auth.UserRequest;
import com.kshrd.assessment.dto.teacher.TeacherResponse;

import java.util.List;

public interface IKeycloakService {
    String createUser(UserRequest userRequest);
    String getUser(String userId);
    LoginResponse login(LoginRequest loginRequest);
    List<TeacherResponse> getAllTeachers();
}
