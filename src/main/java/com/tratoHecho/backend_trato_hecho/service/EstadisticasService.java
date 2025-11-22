package com.tratoHecho.backend_trato_hecho.service;

import com.tratoHecho.backend_trato_hecho.dto.EstadisticasProviderDTO;

public interface EstadisticasService {
    EstadisticasProviderDTO obtenerEstadisticasProveedor(Long userId);
}