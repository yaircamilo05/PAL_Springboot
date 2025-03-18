package com.example.pal.repository;

import com.example.pal.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findByCourseId(Long courseId);
}
