package com.example.pal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.pal.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
} 