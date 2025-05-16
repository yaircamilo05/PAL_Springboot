package com.example.pal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.pal.model.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {


    @Query("SELECT c FROM Course c WHERE " +
            "(:title IS NULL OR c.title LIKE %:title%) AND " +
            "(:description IS NULL OR c.description LIKE %:description%) AND " +
            "(:categoryName IS NULL OR c.category.name LIKE %:categoryName%) AND " +
            "(:free IS NULL OR (:free = TRUE AND c.price = 0) OR (:free = FALSE AND c.price > 0))")
    List<Course> searchCourses(
        @Param("title") String title, 
        @Param("description") String description, 
        @Param("categoryName") String categoryName, 
        @Param("free") Boolean free);
}