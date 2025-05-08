package com.example.pal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pal.dto.CreateEnrollment;
import com.example.pal.service.EnrollmentService;
import com.example.pal.model.Enrollment; // Ensure this import matches the actual package of the Enrollment class

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }
    

@PostMapping("/register")
public ResponseEntity<String> registerUser(@RequestBody CreateEnrollment enrollment) {
    Enrollment enrollmentResult = enrollmentService.enrollUserInCourse(enrollment.getUserId(), enrollment.getCourseId());
    return ResponseEntity.status(201).body(enrollmentResult.toString()); 
}

}