package com.example.pal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pal.model.Enrollment;
import com.example.pal.model.EnrollmentId;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, EnrollmentId> {
    

    boolean existsById(EnrollmentId id);

} 