package com.example.pal.dto;

import java.util.List;
import lombok.Data;

@Data
public class ExamSubmissionDTO {
    private List<AnswerSubmissionDTO> answers;
}
