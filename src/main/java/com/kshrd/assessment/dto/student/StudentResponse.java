package com.kshrd.assessment.dto.student;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentResponse {
    String studentId;
    String studentName;
    String email;
}
