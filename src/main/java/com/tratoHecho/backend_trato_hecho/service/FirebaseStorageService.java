package com.tratoHecho.backend_trato_hecho.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    private final String bucketName;

    public FirebaseStorageService(@Value("${firebase.storage.bucket}") String bucketName) {
        this.bucketName = bucketName;
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FileInputStream serviceAccount = new FileInputStream("src/main/resources/serviceAccountKey.json");
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setStorageBucket(bucketName)
                        .build();
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al inicializar Firebase: " + e.getMessage());
        }
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        InputStream inputStream = file.getInputStream();

        String uuid = UUID.randomUUID().toString();
        // Por defecto preparamos el nombre original
        String fileName = uuid + "-" + originalFilename;

        // --- OPTIMIZACIÓN CON SCRIMAGE (MÓVIL) ---
        // Solo optimizamos si es imagen (y no es SVG o GIF animado que son delicados)
        if (contentType != null && contentType.startsWith("image/") && !contentType.contains("svg")) {
            try {
                // 1. Cargar imagen desde el stream
                ImmutableImage image = ImmutableImage.loader().fromStream(inputStream);

                // 2. Procesar: Redimensionar para MÓVIL + Convertir a WebP
                // CAMBIO: Bajamos a 800px. Suficiente para ver bien en celular vertical.
                // Si la imagen es más pequeña, no la agranda (scale down only).
                // fit() ajusta dentro de la caja 800x800 manteniendo aspecto.
                byte[] compressedData = image.bound(800, 800)
                        .bytes(WebpWriter.DEFAULT.withQ(70)); // Calidad 70% (Buen balance peso/calidad móvil)

                // 3. Reemplazar el stream para subir la versión optimizada
                inputStream = new ByteArrayInputStream(compressedData);

                // Actualizar metadatos
                contentType = "image/webp";
                fileName = uuid + "-" + stripExtension(originalFilename) + ".webp";

            } catch (Exception e) {
                System.err.println("⚠️ Error optimizando con Scrimage: " + e.getMessage() + ". Subiendo original.");
                // Si falla, reseteamos el stream para subir el original
                inputStream = file.getInputStream();
            }
        }

        // --- SUBIDA A FIREBASE ---
        Bucket bucket = StorageClient.getInstance().bucket();
        bucket.create(fileName, inputStream, contentType);

        String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/" + bucketName + "/o/%s?alt=media";
        return String.format(DOWNLOAD_URL, fileName);
    }

    private String stripExtension(String filename) {
        if (filename == null) return "archivo";
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? filename : filename.substring(0, dotIndex);
    }
}