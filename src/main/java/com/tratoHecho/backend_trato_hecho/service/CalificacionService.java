package com.tratoHecho.backend_trato_hecho.service;

import com.tratoHecho.backend_trato_hecho.dto.CalificacionRequestDTO;

public interface CalificacionService {
    void agregarCalificacion(CalificacionRequestDTO calificacionDTO);
    Double obtenerPromedio(Long servicioId);
}