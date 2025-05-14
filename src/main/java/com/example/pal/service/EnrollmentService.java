package com.example.pal.service;

import java.math.BigDecimal;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.pal.model.Enrollment;
import com.example.pal.model.EnrollmentId;
import com.example.pal.model.Payment;
import com.example.pal.model.User;
import com.example.pal.model.Course;
import com.example.pal.repository.EnrollmentRepository;
import com.example.pal.dto.EnrollmentDTO;

import jakarta.transaction.Transactional;

@Service
public class EnrollmentService {

    private final CourseService courseService;
    private final UserService userService;
    private final PaymentService paymentService;
    private final EnrollmentRepository enrollmentRepository;
    private final ModelMapper modelMapper;

    public EnrollmentService(CourseService courseService, UserService userService, PaymentService paymentService, EnrollmentRepository enrollmentRepository, ModelMapper modelMapper) {
        this.courseService = courseService;
        this.userService = userService;
        this.paymentService = paymentService;
        this.enrollmentRepository = enrollmentRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public EnrollmentDTO enrollUserInCourse(Long userId, Long courseId) {
        User user = userService.findById(userId);
        Course course = courseService.findById(courseId);
    
        // Crear el EnrollmentId para validar si ya existe
        EnrollmentId enrollmentId = new EnrollmentId();
        enrollmentId.setUserId(userId);
        enrollmentId.setCourseId(courseId);
    
        // Validar si el usuario ya está inscrito en el curso
        boolean alreadyEnrolled = enrollmentRepository.existsById(enrollmentId);
        if (alreadyEnrolled) {
            throw new IllegalStateException("El usuario ya está inscrito en este curso.");
        }
    
        // Crear la inscripción
        Enrollment enrollment = new Enrollment();
        enrollment.setId(enrollmentId); // Establecer el ID compuesto
        enrollment.setUser(user);
        enrollment.setCourse(course);
        Payment payment = paymentService.createPayment(BigDecimal.valueOf(course.getPrice()));
        // Crear el pago usando PaymentService
        enrollment.setPayment(payment);

        enrollmentRepository.save(enrollment);

        EnrollmentDTO enrollmentDTO = modelMapper.map(enrollment, EnrollmentDTO.class);
        enrollmentDTO.setCourseName(enrollment.getCourse().getTitle());
        enrollmentDTO.setPaymentStatus(enrollment.getPayment().getStatus());

        return enrollmentDTO;
    }


@Transactional
public List<EnrollmentDTO> getAllEnrollmentsByUserId(Long userId) {
    List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
    return enrollments.stream()
            .map(enrollment -> {
                EnrollmentDTO enrollmentDTO = modelMapper.map(enrollment, EnrollmentDTO.class);
                enrollmentDTO.setCourseName(enrollment.getCourse().getTitle());
                enrollmentDTO.setPaymentStatus(enrollment.getPayment().getStatus());
                return enrollmentDTO;
            })
            .toList();
}

}