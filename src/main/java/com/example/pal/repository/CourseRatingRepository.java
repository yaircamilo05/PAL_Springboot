package com.example.pal.repository;

import com.example.pal.model.CourseRating;
import com.example.pal.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRatingRepository extends JpaRepository<CourseRating, Long> {
    @Query("SELECT AVG(r.rating) FROM CourseRating r WHERE r.course = :course")
    Double findAverageRatingByCourse(Course course);
}
