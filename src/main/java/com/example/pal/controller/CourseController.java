package com.example.pal.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.example.pal.service.CourseService;
import com.example.pal.dto.CreateCourseDTO;
import com.example.pal.dto.CourseResponseDTO;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/create")
    public ResponseEntity<CourseResponseDTO> createCourse(@RequestBody CreateCourseDTO courseDTO) {
        System.out.println("📩 Petición recibida en el controlador");
        System.out.println("Datos recibidos: " + courseDTO);
        return ResponseEntity.status(201).body(courseService.createCourse(courseDTO));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @PathVariable("id") Long id,
            @RequestBody CreateCourseDTO courseDTO) {
        return ResponseEntity.ok(courseService.updateCourse(id, courseDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteCourse(@PathVariable("id") Long id) {
        courseService.deleteCourse(id);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Curso eliminado exitosamente");
        return ResponseEntity.ok(response);
    }
} 