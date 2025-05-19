package com.example.pal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "questions")
@Getter
@Setter
@ToString(exclude = {"exam", "options"})
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;
    
    @Column(nullable = false)
    private Double points;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    @JsonIgnore
    private Exam exam;
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("question")
    private List<QuestionOption> options = new ArrayList<>();
    
    // Campo para almacenar la respuesta correcta en caso de preguntas abiertas
    @Column(columnDefinition = "TEXT")
    private String correctAnswer;
    
    // Sobrescribir equals para utilizar solo el ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question)) return false;
        Question question = (Question) o;
        return id != null && id.equals(question.id);
    }
    
    // Sobrescribir hashCode para utilizar solo el ID
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
