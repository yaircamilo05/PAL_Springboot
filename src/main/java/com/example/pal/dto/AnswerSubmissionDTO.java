package com.example.pal.dto;

import lombok.Data;

@Data
public class AnswerSubmissionDTO {
    private Long questionId;
    private Long selectedOptionId;  // Para preguntas de opción múltiple
    private String textAnswer;      // Para preguntas de respuesta corta o texto libre
}
