package com.example.pal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "exams")
@Getter
@Setter
@ToString(exclude = {"questions"})
public class Exam {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = true, length = 1000)
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnoreProperties({"exams", "contents", "instructor"})
    private Course course;
    
    @Column(nullable = false)
    private Double totalPoints;
    
    @Column(nullable = false)
    private Integer timeLimit; // En minutos
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("exam")
    private List<Question> questions = new ArrayList<>();
    
    // Sobrescribir equals para utilizar solo el ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exam)) return false;
        Exam exam = (Exam) o;
        return id != null && id.equals(exam.id);
    }
    
    // Sobrescribir hashCode para utilizar solo el ID
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
