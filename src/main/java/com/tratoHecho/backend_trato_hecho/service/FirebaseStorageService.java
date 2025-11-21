package com.tratoHecho.backend_trato_hecho.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    private final String bucketName;

    public FirebaseStorageService(@Value("${firebase.storage.bucket}") String bucketName) {
        this.bucketName = bucketName;

        try {
            // Verifica si ya hay una instancia de Firebase corriendo para no duplicarla
            if (FirebaseApp.getApps().isEmpty()) {
                // Asegúrate de que este archivo esté en src/main/resources/
                FileInputStream serviceAccount = new FileInputStream("src/main/resources/serviceAccountKey.json");

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setStorageBucket(bucketName) // Usamos la variable inyectada
                        .build();

                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al inicializar Firebase: " + e.getMessage());
        }
    }

    public String uploadFile(MultipartFile file) throws IOException {
        // 1. Generar nombre único
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        // 2. Obtener referencia al bucket
        Bucket bucket = StorageClient.getInstance().bucket();

        // 3. Subir el archivo
        bucket.create(fileName, file.getInputStream(), file.getContentType());

        // 4. Retornar la URL de descarga pública
        String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/" + bucketName + "/o/%s?alt=media";

        return String.format(DOWNLOAD_URL, fileName);
    }
}