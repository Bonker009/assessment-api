package com.kshrd.assessment.controller;

import com.kshrd.assessment.dto.auth.LoginRequest;
import com.kshrd.assessment.dto.auth.LoginResponse;
import com.kshrd.assessment.dto.auth.UserRequest;
import com.kshrd.assessment.dto.response.ApiResponse;
import com.kshrd.assessment.dto.response.ResponseUtil;
import com.kshrd.assessment.service.IKeycloakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v4/auth")
@AllArgsConstructor
@Getter
@Setter
@Tag(name = "Authentication", description = "APIs for user authentication and registration")
public class UserController {
    private final IKeycloakService keycloakService;

    @PostMapping("/register")
    @SecurityRequirements(value = {})
    @Operation(summary = "Register a new user", description = "Creates a new user account in Keycloak. This endpoint does not require authentication.")
    public ResponseEntity<ApiResponse<String>> createUser(@RequestBody @Valid UserRequest userRequest) {
        String result = keycloakService.createUser(userRequest);
        return ResponseUtil.created(result, "User registered successfully");
    }

    @PostMapping("/login")
    @SecurityRequirements(value = {})
    @Operation(summary = "User login", description = "Authenticates a user and returns JWT access token and refresh token. This endpoint does not require authentication.")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest loginRequest) {
        LoginResponse loginResponse = keycloakService.login(loginRequest);
        return ResponseUtil.ok(loginResponse, "Login successful");
    }
}
