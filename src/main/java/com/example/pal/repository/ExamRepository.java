package com.example.pal.repository;

import com.example.pal.model.Course;
import com.example.pal.model.Exam;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findByCourse(Course course);

    Exam findExamById(Long examId);
}
