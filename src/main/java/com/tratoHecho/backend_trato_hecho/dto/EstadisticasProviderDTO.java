package com.tratoHecho.backend_trato_hecho.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstadisticasProviderDTO {
    private Long totalContrataciones;
    private Long totalCalificaciones;
    private Double promedioGeneral;
}