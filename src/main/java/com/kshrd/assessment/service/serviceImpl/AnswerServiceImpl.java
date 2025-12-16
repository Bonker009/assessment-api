package com.kshrd.assessment.service.serviceImpl;

import com.kshrd.assessment.entity.Answer;
import com.kshrd.assessment.service.IAnswerService;
import com.kshrd.assessment.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AnswerServiceImpl implements IAnswerService {

    public Answer createAnswer(Answer answer) {
        UUID studentId = SecurityUtils.getCurrentUserId();
        
        if (studentId == null) {
            throw new IllegalStateException("User is not authenticated");
        }
        
        answer.setStudent_id(studentId);
        
        return answer;
    }

    public boolean isAnswerOwner(UUID answerId) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        
        if (currentUserId == null) {
            return false;
        }
        
        return true;
    }
}
