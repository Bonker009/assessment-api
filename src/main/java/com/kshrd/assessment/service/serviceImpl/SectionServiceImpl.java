package com.kshrd.assessment.service.serviceImpl;

import com.kshrd.assessment.aop.annotation.LogError;
import com.kshrd.assessment.aop.annotation.LogExecution;
import com.kshrd.assessment.aop.annotation.LogPerformance;
import com.kshrd.assessment.repository.SectionRepository;
import com.kshrd.assessment.service.ISectionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@LogExecution(logParameters = true, logReturnValue = true, description = "Section Service")
@LogPerformance(thresholdMillis = 500, description = "Section Service Performance")
@LogError(logStackTrace = true, description = "Section Service Error Handling")
public class SectionServiceImpl implements ISectionService {
    private final SectionRepository sectionRepository;

    @Override
    public Long getTotalSectionsByAssessmentId(UUID assessmentId) {
        return sectionRepository.countByAssessment_Assessment_id(assessmentId);
    }
}
