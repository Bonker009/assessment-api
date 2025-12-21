package com.kshrd.assessment.service;

import org.springframework.stereotype.Service;

import java.util.UUID;


public interface ISectionService {
    Long getTotalSectionsByAssessmentId(UUID assessmentId);
}
