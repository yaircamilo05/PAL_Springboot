package com.example.pal.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pal.dto.CreateEnrollment;
import com.example.pal.dto.EnrollmentDTO;
import com.example.pal.service.EnrollmentService;
import com.example.pal.model.Enrollment; // Ensure this import matches the actual package of the Enrollment class
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }
    

@PostMapping("/register")
public ResponseEntity<EnrollmentDTO> registerUser(@RequestBody CreateEnrollment enrollment) {
    EnrollmentDTO enrollmentResult = enrollmentService.enrollUserInCourse(enrollment.getUserId(), enrollment.getCourseId());
    return ResponseEntity.status(201).body(enrollmentResult); 
}

@GetMapping("/my-courses/{userId}")
public ResponseEntity<List<EnrollmentDTO>> getMyCourses(@PathVariable("userId") Long userId) {
    return ResponseEntity.ok(enrollmentService.getAllEnrollmentsByUserId(userId));
}



}