package com.tratoHecho.backend_trato_hecho.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InboxDTO {
    private Long conId;
    private String chatName;   // Nombre del otro usuario (Mariachi El Sol)
    private String chatImage;  // Foto del otro usuario
    private String lastMessage;// Contenido del último mensaje
    private int unreadCount;   // Para la burbuja individual del chat
    private String subtitle;   // Rol o nombre real
    private double rating;     // Rating del usuario
}