package com.example.pal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pal.model.Enrollment;
import com.example.pal.model.EnrollmentId;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, EnrollmentId> {
    

    boolean existsById(EnrollmentId id);
    List<Enrollment> findByUser_Id(Long userId);

    //conseguir todos los cursos de un usuario
    List<Enrollment> findByUserId(Long userId);
    
    List<Enrollment> findByIdCourseId(Long courseId);



} 