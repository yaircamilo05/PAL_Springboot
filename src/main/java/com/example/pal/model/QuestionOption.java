package com.example.pal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "question_options")
@Getter
@Setter
@ToString(exclude = "question")
public class QuestionOption {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;
    
    @Column(nullable = false)
    private Boolean isCorrect = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private Question question;
    
    // Sobrescribir equals para utilizar solo el ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestionOption)) return false;
        QuestionOption option = (QuestionOption) o;
        return id != null && id.equals(option.id);
    }
    
    // Sobrescribir hashCode para utilizar solo el ID
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
