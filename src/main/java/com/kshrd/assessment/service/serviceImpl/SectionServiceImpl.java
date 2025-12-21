package com.kshrd.assessment.service.serviceImpl;

import com.kshrd.assessment.entity.Section;
import com.kshrd.assessment.repository.SectionRepository;
import com.kshrd.assessment.service.ISectionService;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class SectionServiceImpl implements ISectionService {
    private final SectionRepository sectionRepository;

    @Override
    public Long getTotalSectionsByAssessmentId(UUID assessmentId) {
        return sectionRepository.countByAssessment_Assessment_id(assessmentId);
    }
}
