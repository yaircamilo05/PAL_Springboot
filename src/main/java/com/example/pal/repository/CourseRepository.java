package com.example.pal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.pal.model.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
} 