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
public class ContratarServicioDTO {
    private Long userId;
    private Long servicioId;

    // Formato esperado JSON: "2025-11-22T14:30:00"
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
}