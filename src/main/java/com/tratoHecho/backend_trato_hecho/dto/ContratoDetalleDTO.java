package com.tratoHecho.backend_trato_hecho.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContratoDetalleDTO {

    private Long contratoId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    // Datos de la contraparte (Si soy comprador, aquí va el vendedor. Si soy vendedor, va el cliente)
    private Long contraparteId;
    private String contraparteNombre;
    private String contraparteFoto;
    private String contraparteRolEnTransaccion; // "VENDEDOR" o "COMPRADOR"

    // El servicio completo (con fotos, promedios, etc.)
    private ServicioResponseDTO servicio;

    private Boolean yaCalificado;
    private Integer miNota;
}