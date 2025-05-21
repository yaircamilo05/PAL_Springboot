package com.example.pal.dto;

import com.example.pal.model.QuestionType;
import lombok.Data;
import java.util.List;

@Data
public class QuestionDTO {
    private Long id;
    private String text;
    private Double points;
    private QuestionType type;
    
    // Para preguntas de respuesta corta o ensayo
    private String correctAnswer;
    
    // Para preguntas de opción múltiple
    private List<QuestionOptionDTO> options;
}
