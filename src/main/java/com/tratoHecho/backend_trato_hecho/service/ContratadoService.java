package com.tratoHecho.backend_trato_hecho.service;

import com.tratoHecho.backend_trato_hecho.dto.ContratarServicioDTO;
import com.tratoHecho.backend_trato_hecho.dto.HistorialTransaccionesDTO;

public interface ContratadoService {
    void contratarServicio(ContratarServicioDTO contratacionDTO);

    HistorialTransaccionesDTO obtenerHistorialPorUsuario(Long userId);
}