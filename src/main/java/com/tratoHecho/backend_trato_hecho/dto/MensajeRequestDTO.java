package com.tratoHecho.backend_trato_hecho.dto;

import lombok.Data;

@Data
public class MensajeRequestDTO {
    private Long senderId;    // Quién envía (Pedro)
    private Long receiverId;  // Quién recibe (Mariachi) - Opcional si ya tienes conId
    private Long conId;       // ID de la conversación (Si ya existe)
    private String contenido; // "Hola"
    private Long serId;      // ID del servicio relacionado
}