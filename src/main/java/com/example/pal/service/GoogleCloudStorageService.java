package com.example.pal.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class GoogleCloudStorageService {

    private final Storage storage;

    @Value("${gcp.storage.bucket-name}")
    private String bucketName;

    public GoogleCloudStorageService(Storage storage) {
        this.storage = storage;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        Bucket bucket = storage.get(bucketName);
        Blob blob = bucket.create(file.getOriginalFilename(), file.getBytes(), file.getContentType());
        return blob.getMediaLink(); // URL del archivo subido
    }

    public byte[] downloadFile(String fileName) {
        Blob blob = storage.get(bucketName, fileName);
        return blob.getContent();
    }

    public void deleteFile(String fileName) {
        storage.get(bucketName, fileName).delete();
    }

    public void saveFileLocally(String fileName, String localPath) throws IOException {
        Blob blob = storage.get(bucketName, fileName);
        Path destination = Path.of(localPath, fileName);
        Files.write(destination, blob.getContent());
    }
}