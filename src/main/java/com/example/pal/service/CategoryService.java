package com.example.pal.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.pal.repository.CategoryRepository;
import com.example.pal.model.Category;
import com.example.pal.dto.CreateCategoryDTO;
import com.example.pal.dto.CategoryDTO;
import org.modelmapper.ModelMapper;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryService(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    public CategoryDTO createCategory(CreateCategoryDTO createCategoryDTO) {
        Category category = new Category();
        category.setName(createCategoryDTO.getName());
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
            .map(category -> modelMapper.map(category, CategoryDTO.class))
            .toList();
    }

    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        return modelMapper.map(category, CategoryDTO.class);
    }

    public CategoryDTO updateCategory(Long id, CreateCategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        
        category.setName(categoryDTO.getName());
        Category updatedCategory = categoryRepository.save(category);
        return modelMapper.map(updatedCategory, CategoryDTO.class);
    }

    public Map<String, String> deleteCategory(Long id) {
        categoryRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Categoría eliminada exitosamente");
        return response;
    }
} 