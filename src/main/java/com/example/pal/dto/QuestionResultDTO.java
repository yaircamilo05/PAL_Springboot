package com.example.pal.dto;

import lombok.Data;

@Data
public class QuestionResultDTO {
    private Long questionId;
    private String questionText;
    private String questionType;
    private String userAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
    private Double pointsEarned;
    private Double maxPoints;
    private String feedback;
}
