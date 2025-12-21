package com.kshrd.assessment.controller;

import com.kshrd.assessment.dto.response.ApiResponse;
import com.kshrd.assessment.dto.response.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
public class HealthController {

    @GetMapping("/inform")
    @PostMapping("/inform")
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        Map<String, String> healthStatus = Map.of("status", "UP", "service", "assessment-management");
        return ResponseUtil.ok(healthStatus, "Service is healthy");
    }
}
