package com.example.pal.config;

import com.example.pal.dto.*;
import com.example.pal.model.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        // Mapear User → UserResponseDTO
        TypeMap<User, UserResponseDTO> userMap = mapper.createTypeMap(User.class, UserResponseDTO.class);
        userMap.addMapping(User::getId, UserResponseDTO::setId);
        userMap.addMapping(User::getUsername, UserResponseDTO::setUsername);

        // Mapear Category → CategoryDTO
        TypeMap<Category, CategoryDTO> categoryMap = mapper.createTypeMap(Category.class, CategoryDTO.class);
        categoryMap.addMapping(Category::getId, CategoryDTO::setId);
        categoryMap.addMapping(Category::getName, CategoryDTO::setName);

        // Mapear Course → CourseDetailsDTO (sin conflictos automáticos)
        mapper.createTypeMap(Course.class, CourseDetailsDTO.class);

        return mapper;
    }
}
