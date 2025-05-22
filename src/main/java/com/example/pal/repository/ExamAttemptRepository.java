package com.example.pal.repository;

import com.example.pal.model.ExamAttempt;
import com.example.pal.model.User;
import com.example.pal.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    List<ExamAttempt> findByStudent(User student);
    List<ExamAttempt> findByExamIdAndStudentId(Long examId, Long studentId);
    List<ExamAttempt> findByExam(Exam exam);
    List<ExamAttempt> findByStudentAndExam(User student, Exam exam);
    Optional<ExamAttempt> findTopByStudentAndExamOrderByStartTimeDesc(User student, Exam exam);
}
