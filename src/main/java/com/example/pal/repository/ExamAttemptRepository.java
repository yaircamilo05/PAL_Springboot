package com.example.pal.repository;

import com.example.pal.model.ExamAttempt;
import com.example.pal.model.ExamStatus;
import com.example.pal.model.User;
import com.example.pal.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    List<ExamAttempt> findByStudent(User student);
    List<ExamAttempt> findByExam(Exam exam);
    List<ExamAttempt> findByStudentAndExam(User student, Exam exam);
    Optional<ExamAttempt> findTopByStudentAndExamOrderByStartTimeDesc(User student, Exam exam);
    
    // Todas las intentos de un estudiante
    List<ExamAttempt> findByStudentId(Long studentId);

    // Todas las intentos de un estudiante en un examen específico
    List<ExamAttempt> findByExamIdAndStudentId(Long examId, Long studentId);

    // Todas las intentos con un estado específico para un estudiante
    List<ExamAttempt> findByStudentIdAndStatus(Long studentId, ExamStatus status);

    // Obtener los intentos filtrando por estado y por examen específico
    List<ExamAttempt> findByExamIdAndStudentIdAndStatus(Long examId, Long studentId, ExamStatus status);
}