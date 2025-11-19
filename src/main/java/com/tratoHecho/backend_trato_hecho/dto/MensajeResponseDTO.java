package com.tratoHecho.backend_trato_hecho.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class MensajeResponseDTO {
    private Long msjId;
    private String contenido;
    private LocalDateTime fechaEnvio;
    private Long senderId;    // Para saber si es "me" o "other"
    private String senderName;
}