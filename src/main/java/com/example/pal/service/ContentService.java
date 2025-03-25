package com.example.pal.service;

import com.google.cloud.storage.*;
import com.example.pal.model.Content;
import com.example.pal.model.Course;
import com.example.pal.repository.ContentRepository;
import com.example.pal.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ContentService {

    private final Storage storage;
    private final ContentRepository contentRepository;
    private final CourseRepository courseRepository;

    @Value("${gcp.storage.bucket-name}")
    private String bucketName;

    public ContentService(Storage storage, ContentRepository contentRepository, CourseRepository courseRepository) {
        this.storage = storage;
        this.contentRepository = contentRepository;
        this.courseRepository = courseRepository;
    }

    /**
     *
     * @param courseId
     * @param file
     * @return
     * @throws IOException
     */
    public Content uploadContent(Long courseId, MultipartFile file) throws IOException {
        // Verificar si el curso existe
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            throw new IllegalArgumentException("El curso con ID " + courseId + " no existe.");
        }
        Course course = courseOpt.get();

        // Subir el archivo a Google Cloud Storage
        String fileName = file.getOriginalFilename();
        Blob blob = storage.get(bucketName).create(fileName, file.getBytes(), file.getContentType());

        // Guardar en la base de datos
        Content content = new Content();
        content.setType(file.getContentType());
        content.setUrl(blob.getMediaLink());
        content.setCourse(course);

        return contentRepository.save(content);
    }

    /**
     *
     * @return
     */
    public List<Content> getAllContent() {
        return contentRepository.findAll();
    }

    /**
     *
     * @param id
     * @return
     */
    public Optional<Content> getContentById(Long id) {
        return contentRepository.findById(id);
    }

    /**
     *
     * @param id
     * @param newType
     * @param newFile
     * @return
     * @throws IOException
     */
    public Content updateContent(Long id, String newType, MultipartFile newFile) throws IOException {
        Optional<Content> optionalContent = contentRepository.findById(id);
        if (optionalContent.isPresent()) {
            Content content = optionalContent.get();

            // Subir nuevo archivo si se proporciona
            if (newFile != null) {
                String fileName = newFile.getOriginalFilename();
                Blob blob = storage.get(bucketName).create(fileName, newFile.getBytes(), newFile.getContentType());
                content.setUrl(blob.getMediaLink());
            }

            if (newType != null) {
                content.setType(newType);
            }

            return contentRepository.save(content);
        }
        return null;
    }

    public boolean deleteContent(Long id) {
        try {
            Optional<Content> optionalContent = contentRepository.findById(id);
            if (optionalContent.isPresent()) {
                Content content = optionalContent.get();

                // Verificar si el archivo existe antes de eliminarlo
                Blob blob = storage.get(bucketName, content.getUrl());
                if (blob != null) {
                    blob.delete();
                } else {
                    System.err.println("Archivo no encontrado en el bucket: " + content.getUrl());
                }

                contentRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error eliminando contenido", e);
        }
    }

}
