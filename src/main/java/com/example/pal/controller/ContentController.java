package com.example.pal.controller;

import org.springframework.beans.factory.annotation.Value;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.storage.Blob;
import com.example.pal.model.Content;
import com.example.pal.service.ContentService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/content")
public class ContentController {

    private final ContentService contentService;

    private final Storage storage;

    @Value("${gcp.storage.bucket-name}")
    private String bucketName;

    public ContentController(Storage storage, ContentService contentService) {
        this.storage = storage;
        this.contentService = contentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Content> uploadContent(
            @RequestParam("courseId") String courseId,
            @RequestParam("file") MultipartFile file) throws IOException {

        Long courseIdLong = Long.parseLong(courseId); // Conversión
        Content content = contentService.uploadContent(courseIdLong, file);

        return ResponseEntity.ok(content);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Content>> getAllContent() {
        return ResponseEntity.ok(contentService.getAllContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Content> getContentById(@PathVariable Long id) {
        Optional<Content> content = contentService.getContentById(id);
        return content.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Content> updateContent(
            @PathVariable Long id,
            @RequestParam(value = "newType", required = false) String newType,
            @RequestParam(value = "newFile", required = false) MultipartFile newFile) throws IOException {
        Content updatedContent = contentService.updateContent(id, newType, newFile);
        return updatedContent != null ? ResponseEntity.ok(updatedContent) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteContent(@PathVariable Long id) {
        boolean deleted = contentService.deleteContent(id);
        return deleted ? ResponseEntity.ok("Contenido eliminado") : ResponseEntity.notFound().build();
    }

    @PostMapping("/test-upload")
    public ResponseEntity<String> testUploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo está vacío");
        }

        try {
            // Verificar la configuración
            System.out.println("Intentando acceder al bucket: " + bucketName);

            // Obtener el bucket
            Bucket bucket = storage.get(bucketName);
            if (bucket == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("No se pudo acceder al bucket: " + bucketName);
            }

            // Subir archivo a Google Cloud Storage con un nombre único
            String originalFileName = file.getOriginalFilename();
            String fileName = System.currentTimeMillis() + "_" + originalFileName;
            System.out.println("Subiendo archivo: " + fileName + " de tipo: " + file.getContentType());

            Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());

            // Retornar la URL del archivo subido
            return ResponseEntity.ok("Archivo subido exitosamente. Nombre: " + fileName +
                    ", Tamaño: " + file.getSize() + " bytes, URL: " + blob.getMediaLink());
        } catch (Exception e) {
            e.printStackTrace(); // Esto te permitirá ver el error completo en los logs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir archivo: " + e.getMessage() + " | Tipo: " + e.getClass().getName());
        }
    }
}
