package com.example.pal.service;

import com.example.pal.dto.CertificateResponseDTO;
import com.example.pal.model.*;
import com.example.pal.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final CourseRepository courseRepository;
    private final ExamRepository examRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public CertificateResponseDTO generateCertificate(Long courseId, Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        List<Exam> courseExams = examRepository.findByCourseId(courseId);

        for (Exam exam : courseExams) {
            List<ExamAttempt> attempts = examAttemptRepository.findByExamIdAndStudentId(exam.getId(), studentId);

            boolean passed = attempts.stream().anyMatch(attempt ->
                    (attempt.getStatus() == ExamStatus.SUBMITTED || attempt.getStatus() == ExamStatus.GRADED) &&
                    attempt.getScore() >= (0.6 * attempt.getMaxScore())
            );

            if (!passed) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No puedes generar el certificado aún. Debes aprobar todos los exámenes del curso.");
            }
        }

        Certificate certificate = new Certificate();
        certificate.setStudent(student);
        certificate.setCourse(course);
        certificate.setIssuedAt(LocalDateTime.now());

        String text = "Certificamos que " + student.getUsername() + " ha completado satisfactoriamente el curso " +
                "\"" + course.getTitle() + "\" en la plataforma PAL el día " +
                LocalDateTime.now().toLocalDate() + ".";

        certificate.setContentText(text);

        Certificate saved = certificateRepository.save(certificate);

        CertificateResponseDTO dto = new CertificateResponseDTO();
        dto.setId(saved.getId());
        dto.setContentText(saved.getContentText());
        dto.setIssuedAt(saved.getIssuedAt());
        dto.setStudentName(student.getUsername());
        dto.setCourseTitle(course.getTitle());
        return dto;
    }

    public Certificate getCertificateById(Long id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificado no encontrado"));
    }

    public List<CertificateResponseDTO> getCertificatesForStudent(Long studentId) {
        return certificateRepository.findByStudentId(studentId).stream()
                .map(cert -> {
                    CertificateResponseDTO dto = new CertificateResponseDTO();
                    dto.setId(cert.getId());
                    dto.setContentText(cert.getContentText());
                    dto.setIssuedAt(cert.getIssuedAt());
                    dto.setCourseTitle(cert.getCourse().getTitle());
                    dto.setStudentName(cert.getStudent().getUsername());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
