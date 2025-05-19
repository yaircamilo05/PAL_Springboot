package com.example.pal.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "student_answers")
@Data
public class StudentAnswer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    @JsonIgnore
    private ExamAttempt attempt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    @Column(columnDefinition = "TEXT")
    private String textAnswer; // Para preguntas de tipo SHORT_ANSWER o ESSAY
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private QuestionOption selectedOption; // Para preguntas de tipo MULTIPLE_CHOICE, TRUE_FALSE
    
    @Column
    private Boolean isCorrect;
    
    @Column
    private Double pointsEarned = 0.0;
    
    @Column(columnDefinition = "TEXT")
    private String feedback;
}
