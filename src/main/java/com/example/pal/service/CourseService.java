package com.example.pal.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.pal.repository.CourseRepository;
import com.example.pal.repository.CategoryRepository;
import com.example.pal.repository.UserRepository;
import com.example.pal.model.Course;
import com.example.pal.model.Category;
import com.example.pal.model.User;
import com.example.pal.dto.CreateCourseDTO;
import com.example.pal.dto.CourseResponseDTO;
import org.modelmapper.ModelMapper;
import java.util.List;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public CourseService(CourseRepository courseRepository, 
                        CategoryRepository categoryRepository,
                        UserRepository userRepository,
                        ModelMapper modelMapper) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    private void validateInstructor(User user) {
        boolean isInstructor = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("INSTRUCTOR"));
        if (!isInstructor) {
            throw new RuntimeException("El usuario no tiene el rol de instructor");
        }
    }

    public CourseResponseDTO createCourse(CreateCourseDTO createCourseDTO) {
        System.out.println("Iniciando creación de curso...");

        Course course = new Course();
        course.setTitle(createCourseDTO.getTitle());
        course.setDescription(createCourseDTO.getDescription());
        course.setPrice(createCourseDTO.getPrice());

        System.out.println("Buscando categoría con ID: " + createCourseDTO.getCategoryId());
        Category category = categoryRepository.findById(createCourseDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        course.setCategory(category);

        System.out.println("Buscando instructor con ID: " + createCourseDTO.getInstructorId());
        User instructor = userRepository.findById(createCourseDTO.getInstructorId())
                .orElseThrow(() -> new RuntimeException("Instructor no encontrado"));

        System.out.println("Validando instructor...");
        validateInstructor(instructor);
        course.setInstructor(instructor);

        System.out.println("Guardando curso en la base de datos...");
        Course savedCourse = courseRepository.save(course);

        System.out.println("Curso creado exitosamente con ID: " + savedCourse.getId());
        return modelMapper.map(savedCourse, CourseResponseDTO.class);
    }


    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
            .map(course -> modelMapper.map(course, CourseResponseDTO.class))
            .toList();
    }

    @Transactional(readOnly = true)
    public CourseResponseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        return modelMapper.map(course, CourseResponseDTO.class);
    }

    public CourseResponseDTO updateCourse(Long id, CreateCourseDTO courseDTO) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        
        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        course.setPrice(courseDTO.getPrice());
        
        if (courseDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(courseDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            course.setCategory(category);
        }
        
        if (courseDTO.getInstructorId() != null) {
            User instructor = userRepository.findById(courseDTO.getInstructorId())
                .orElseThrow(() -> new RuntimeException("Instructor no encontrado"));
            validateInstructor(instructor);
            course.setInstructor(instructor);
        }
        
        Course updatedCourse = courseRepository.save(course);
        return modelMapper.map(updatedCourse, CourseResponseDTO.class);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }
} 