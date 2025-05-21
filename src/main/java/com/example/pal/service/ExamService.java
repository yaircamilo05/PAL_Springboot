package com.example.pal.service;

import com.example.pal.dto.*;
import com.example.pal.model.*;
import com.example.pal.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final UserService userService;
    private final CourseService courseService;
    private final ModelMapper modelMapper;

    public ExamService(
            ExamRepository examRepository,
            QuestionRepository questionRepository,
            ExamAttemptRepository examAttemptRepository,
            StudentAnswerRepository studentAnswerRepository,
            UserService userService,
            CourseService courseService,
            ModelMapper modelMapper) {
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.examAttemptRepository = examAttemptRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.userService = userService;
        this.courseService = courseService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public Exam createExam(CreateExamDTO createExamDTO) {
        // Crear el examen
        Exam exam = new Exam();
        exam.setTitle(createExamDTO.getTitle());
        exam.setDescription(createExamDTO.getDescription());
        exam.setCourse(courseService.findById(createExamDTO.getCourseId()));
        exam.setTimeLimit(createExamDTO.getTimeLimit());
        exam.setTotalPoints(createExamDTO.getTotalPoints());
        exam.setActive(createExamDTO.getActive() != null ? createExamDTO.getActive() : true);
        
        // Guardar el examen primero para obtener el ID
        exam = examRepository.save(exam);
        
        // Crear las preguntas
        List<Question> questions = new ArrayList<>();
        if (createExamDTO.getQuestions() != null) {
            for (QuestionDTO questionDTO : createExamDTO.getQuestions()) {
                Question question = new Question();
                question.setText(questionDTO.getText());
                question.setPoints(questionDTO.getPoints());
                question.setType(questionDTO.getType());
                question.setCorrectAnswer(questionDTO.getCorrectAnswer());
                question.setExam(exam);
                
                // Si tiene opciones, crearlas
                if (questionDTO.getOptions() != null && !questionDTO.getOptions().isEmpty()) {
                    for (QuestionOptionDTO optionDTO : questionDTO.getOptions()) {
                        QuestionOption option = new QuestionOption();
                        option.setText(optionDTO.getText());
                        option.setIsCorrect(optionDTO.getIsCorrect());
                        option.setQuestion(question);
                        question.getOptions().add(option);
                    }
                }
                
                questions.add(question);
            }
        }
        
        exam.setQuestions(questions);
        return examRepository.save(exam);
    }

    @Transactional(readOnly = true)
    public Exam getExamById(Long examId) {
        return examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Examen no encontrado con id: " + examId));
    }

    @Transactional
    public ExamAttemptDTO startExam(Long examId, Long userId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Examen no encontrado con id: " + examId));
        User student = userService.findById(userId);
        
        // Verificar si el estudiante ya tiene un intento en progreso
        examAttemptRepository.findByStudentAndExam(student, exam).stream()
                .filter(attempt -> attempt.getStatus() == ExamStatus.IN_PROGRESS)
                .findFirst()
                .ifPresent(attempt -> {
                    throw new RuntimeException("Ya tienes un examen en progreso. Debes terminar o cancelar ese intento.");
                });
        
        // Crear un nuevo intento
        ExamAttempt attempt = new ExamAttempt();
        attempt.setStudent(student);
        attempt.setExam(exam);
        attempt.setStartTime(LocalDateTime.now());
        attempt.setStatus(ExamStatus.IN_PROGRESS);
        attempt.setMaxScore(exam.getTotalPoints());
        
        attempt = examAttemptRepository.save(attempt);
        
        // Convertir a DTO
        ExamAttemptDTO dto = new ExamAttemptDTO();
        dto.setId(attempt.getId());
        dto.setExamId(exam.getId());
        dto.setExamTitle(exam.getTitle());
        dto.setStartTime(attempt.getStartTime());
        dto.setTimeLimit(exam.getTimeLimit());
        dto.setTotalQuestions(exam.getQuestions().size());
        dto.setQuestions(this.getExamQuestions(examId));
        
        return dto;
    }

    @Transactional
    public ExamResultDTO submitExam(Long examId, Long userId, ExamSubmissionDTO submission) {
        // Obtener el examen y el usuario
        Exam exam = examRepository.findExamById(examId);
        User student = userService.findById(userId);
        
        // Buscar el intento en progreso más reciente del estudiante
        ExamAttempt attempt = examAttemptRepository.findTopByStudentAndExamOrderByStartTimeDesc(student, exam)
                .filter(a -> a.getStatus() == ExamStatus.IN_PROGRESS)
                .orElseThrow(() -> new RuntimeException("No se encontró un examen en progreso para este estudiante"));
        
        // Verificar si ha expirado el tiempo límite
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timeLimit = attempt.getStartTime().plusMinutes(exam.getTimeLimit());
        if (now.isAfter(timeLimit)) {
            attempt.setStatus(ExamStatus.EXPIRED);
            attempt.setSubmissionTime(now);
            examAttemptRepository.save(attempt);
            throw new RuntimeException("El tiempo para este examen ha expirado");
        }
        
        // Registrar hora de envío
        attempt.setSubmissionTime(now);
        attempt.setStatus(ExamStatus.SUBMITTED);
        
        // Procesar las respuestas y calcular la puntuación
        double totalScore = 0.0;
        boolean needsManualGrading = false;
        
        for (AnswerSubmissionDTO answerDTO : submission.getAnswers()) {
            Question question = questionRepository.findById(answerDTO.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Pregunta no encontrada con id: " + answerDTO.getQuestionId()));
            
            StudentAnswer answer = new StudentAnswer();
            answer.setAttempt(attempt);
            answer.setQuestion(question);
            
            // Procesar según el tipo de pregunta
            switch (question.getType()) {
                case MULTIPLE_CHOICE:
                case TRUE_FALSE:
                    if (answerDTO.getSelectedOptionId() != null) {
                        QuestionOption selectedOption = question.getOptions().stream()
                                .filter(opt -> opt.getId().equals(answerDTO.getSelectedOptionId()))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("Opción no encontrada"));
                        
                        answer.setSelectedOption(selectedOption);
                        answer.setIsCorrect(selectedOption.getIsCorrect());
                        
                        if (selectedOption.getIsCorrect()) {
                            answer.setPointsEarned(question.getPoints());
                            totalScore += question.getPoints();
                        } else {
                            answer.setPointsEarned(0.0);
                        }
                    } else {
                        answer.setIsCorrect(false);
                        answer.setPointsEarned(0.0);
                    }
                    break;
                    
                case SHORT_ANSWER:
                    answer.setTextAnswer(answerDTO.getTextAnswer());
                    
                    // Comparación simple para respuestas cortas
                    if (answerDTO.getTextAnswer() != null && 
                            question.getCorrectAnswer() != null &&
                            answerDTO.getTextAnswer().trim().equalsIgnoreCase(question.getCorrectAnswer().trim())) {
                        answer.setIsCorrect(true);
                        answer.setPointsEarned(question.getPoints());
                        totalScore += question.getPoints();
                    } else {
                        answer.setIsCorrect(false);
                        answer.setPointsEarned(0.0);
                    }
                    break;
                    
                case ESSAY:
                    answer.setTextAnswer(answerDTO.getTextAnswer());
                    // Las preguntas tipo ensayo requieren evaluación manual
                    answer.setIsCorrect(null);
                    answer.setPointsEarned(0.0);
                    needsManualGrading = true;
                    break;
                    
                default:
                    answer.setIsCorrect(false);
                    answer.setPointsEarned(0.0);
            }
            
            studentAnswerRepository.save(answer);
            attempt.getAnswers().add(answer);
        }
        
        // Actualizar la puntuación y el estado del intento
        attempt.setScore(totalScore);
        if (needsManualGrading) {
            attempt.setStatus(ExamStatus.SUBMITTED); // Esperando evaluación manual
        } else {
            attempt.setStatus(ExamStatus.GRADED);
            
            // Calcular retroalimentación general automática
            double percentageScore = (totalScore / exam.getTotalPoints()) * 100;
            if (percentageScore >= 90) {
                attempt.setFeedback("¡Excelente trabajo! Has demostrado un dominio sobresaliente del material.");
            } else if (percentageScore >= 80) {
                attempt.setFeedback("Muy buen trabajo. Tienes un buen entendimiento de los conceptos.");
            } else if (percentageScore >= 70) {
                attempt.setFeedback("Buen trabajo. Hay áreas donde podrías mejorar tu comprensión.");
            } else if (percentageScore >= 60) {
                attempt.setFeedback("Aprobado. Revisa las preguntas que fallaste para reforzar tu conocimiento.");
            } else {
                attempt.setFeedback("Necesitas estudiar más este material. Revisa detenidamente las respuestas correctas.");
            }
        }
        
        examAttemptRepository.save(attempt);
        
        // Devolver el resultado
        return buildExamResultDTO(attempt);
    }

    @Transactional(readOnly = true)
    public ExamResultDTO getExamResult(Long examId, Long userId) {
        // Obtener el examen y el usuario
        Exam exam = getExamById(examId);
        User student = userService.findById(userId);
        
        // Buscar el intento más reciente completado
        ExamAttempt attempt = examAttemptRepository.findTopByStudentAndExamOrderByStartTimeDesc(student, exam)
                .filter(a -> a.getStatus() != ExamStatus.IN_PROGRESS)
                .orElseThrow(() -> new RuntimeException("No se encontró ningún examen completado para este estudiante"));
        
        return buildExamResultDTO(attempt);
    }
    
    private ExamResultDTO buildExamResultDTO(ExamAttempt attempt) {
        ExamResultDTO resultDTO = new ExamResultDTO();
        resultDTO.setAttemptId(attempt.getId());
        resultDTO.setExamTitle(attempt.getExam().getTitle());
        resultDTO.setScore(attempt.getScore());
        resultDTO.setMaxScore(attempt.getMaxScore());
        resultDTO.setPercentageScore((attempt.getScore() / attempt.getMaxScore()) * 100);
        resultDTO.setStatus(attempt.getStatus().toString());
        resultDTO.setFeedback(attempt.getFeedback());
        resultDTO.setStartTime(attempt.getStartTime());
        resultDTO.setSubmissionTime(attempt.getSubmissionTime());
        
        // Preparar los resultados por pregunta
        List<QuestionResultDTO> questionResults = attempt.getAnswers().stream().map(answer -> {
            QuestionResultDTO qrDTO = new QuestionResultDTO();
            qrDTO.setQuestionId(answer.getQuestion().getId());
            qrDTO.setQuestionText(answer.getQuestion().getText());
            qrDTO.setQuestionType(answer.getQuestion().getType().toString());
            
            if (answer.getTextAnswer() != null) {
                qrDTO.setUserAnswer(answer.getTextAnswer());
            } else if (answer.getSelectedOption() != null) {
                qrDTO.setUserAnswer(answer.getSelectedOption().getText());
            }
            
            // Dependiendo del tipo de pregunta, mostrar la respuesta correcta
            if (answer.getQuestion().getType() == QuestionType.SHORT_ANSWER || 
                    answer.getQuestion().getType() == QuestionType.ESSAY) {
                qrDTO.setCorrectAnswer(answer.getQuestion().getCorrectAnswer());
            } else {
                // Para preguntas de opción múltiple, encontrar la opción correcta
                String correctOption = answer.getQuestion().getOptions().stream()
                        .filter(QuestionOption::getIsCorrect)
                        .map(QuestionOption::getText)
                        .collect(Collectors.joining(", "));
                qrDTO.setCorrectAnswer(correctOption);
            }
            
            qrDTO.setIsCorrect(answer.getIsCorrect());
            qrDTO.setPointsEarned(answer.getPointsEarned());
            qrDTO.setMaxPoints(answer.getQuestion().getPoints());
            qrDTO.setFeedback(answer.getFeedback());
            
            return qrDTO;
        }).collect(Collectors.toList());
        
        resultDTO.setQuestionResults(questionResults);
        
        return resultDTO;
    }

    @Transactional(readOnly = true)
    public List<QuestionDTO> getExamQuestions(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Examen no encontrado con id: " + examId));
        
        // Convertir a DTOs para no enviar las respuestas correctas al cliente
        return exam.getQuestions().stream().map(question -> {
            QuestionDTO dto = new QuestionDTO();
            dto.setId(question.getId());
            dto.setText(question.getText());
            dto.setPoints(question.getPoints());
            dto.setType(question.getType());
            
            // Para preguntas de opción múltiple, incluir opciones
            if (question.getOptions() != null && !question.getOptions().isEmpty()) {
                List<QuestionOptionDTO> optionDTOs = question.getOptions().stream()
                    .map(option -> {
                        QuestionOptionDTO optionDTO = new QuestionOptionDTO();
                        optionDTO.setId(option.getId());
                        optionDTO.setText(option.getText());
                        // No incluir si la opción es correcta
                        optionDTO.setIsCorrect(null);
                        return optionDTO;
                    })
                    .collect(Collectors.toList());
                dto.setOptions(optionDTOs);
            }
            
            // No enviar respuestas correctas al cliente
            dto.setCorrectAnswer(null);
            
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExamResultDTO> getAllExamResults(Long courseId, Long userId) {
        Course course = courseService.findById(courseId);
        User student = userService.findById(userId);

        List<Exam> exams = examRepository.findByCourse(course);

        // Obtener los resultados de los exámenes para el estudiante
        List<ExamResultDTO> results = new ArrayList<>();
        for (Exam exam : exams) {
            ExamAttempt attempt = examAttemptRepository.findTopByStudentAndExamOrderByStartTimeDesc(student, exam)
                .filter(a -> a.getStatus() != ExamStatus.IN_PROGRESS)
                .orElseThrow(() -> new RuntimeException("No se encontró ningún examen completado para este estudiante"));
            ExamResultDTO resultDTO = buildExamResultDTO(attempt);
            results.add(resultDTO);
        }

        return results;
    }

}
