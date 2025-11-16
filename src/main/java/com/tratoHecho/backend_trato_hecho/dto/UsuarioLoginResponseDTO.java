package com.tratoHecho.backend_trato_hecho.dto;

import com.tratoHecho.backend_trato_hecho.model.*; // Importa los modelos
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class UsuarioLoginResponseDTO {
    // Campos del Usuario
    private Long userId;
    private Long rolId; // Solo el ID del Rol, o un DTO de Rol más simple
    private String userNombreCompleto;
    private String userCorreo;
    private String userGenero;
    private LocalDate userFechaNacimiento;
    private String userTelefono;
    private String userNombreUsuario;
    private Boolean userEstadoVerificado;
    private Boolean userEstado;

    // Colecciones del Usuario (puedes usar las entidades completas aquí,
    // o idealmente DTOs de Servicio, Mensaje, etc., si quieres limpiar la respuesta)

    private Set<Servicio> servicios;
    private Set<Favorito> favoritos;
    private Set<ConversacionUsuario> conversaciones;
    private Set<Mensaje> mensajesRecibidos;

    // NOTA: Para un entorno de producción, sería mejor usar DTOs para Servicio, Mensaje, etc.,
    // para evitar exponer la estructura interna de la base de datos y evitar la serialización excesiva.
}