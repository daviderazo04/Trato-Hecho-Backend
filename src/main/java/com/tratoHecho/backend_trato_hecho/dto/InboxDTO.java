package com.tratoHecho.backend_trato_hecho.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InboxDTO {
    private Long conId;
    private String chatName;    // Ahora será el nombre del SERVICIO (ej: "Payaso Bombón")
    private String chatImage;   // Foto del SERVICIO (o del usuario si no hay servicio)
    private String lastMessage;
    private int unreadCount;
    private String subtitle;    // Nombre del PROVEEDOR (ej: "Jhon Tonsupa")
    private double rating;
    private Long serviceId;     // ID del servicio vinculado
}