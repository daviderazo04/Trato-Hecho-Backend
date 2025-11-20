package com.tratoHecho.backend_trato_hecho.dto;

import com.tratoHecho.backend_trato_hecho.model.*; // Importa los modelos
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class UsuarioLoginResponseDTO {
    private Long userId;

    private String userRol;
    private String userFotoPerfil;

    private String userNombreCompleto;
    private String userCorreo;
    private String userGenero;
    private LocalDate userFechaNacimiento;
    private String userTelefono;
    private String userNombreUsuario;
    private Boolean userEstadoVerificado;
    private Boolean userEstado;


    private Set<Servicio> servicios;
    private Set<Favorito> favoritos;
    private Set<ConversacionUsuario> conversaciones;
    private Set<Mensaje> mensajesRecibidos;

}