package com.tratoHecho.backend_trato_hecho.service.impl;

import com.tratoHecho.backend_trato_hecho.dto.EstadisticasProviderDTO;
import com.tratoHecho.backend_trato_hecho.repository.CalificacionRepository;
import com.tratoHecho.backend_trato_hecho.repository.ContratadoRepository;
import com.tratoHecho.backend_trato_hecho.service.EstadisticasService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstadisticasServiceImpl implements EstadisticasService {

    private final ContratadoRepository contratadoRepository;
    private final CalificacionRepository calificacionRepository;

    @Override
    @Transactional(readOnly = true)
    public EstadisticasProviderDTO obtenerEstadisticasProveedor(Long userId) {

        // 1. Obtener total de veces contratado (Ventas)
        long totalContrataciones = contratadoRepository.countByServicio_Usuario_UserId(userId);

        // 2. Obtener total de reseñas recibidas
        long totalCalificaciones = calificacionRepository.countByServicio_Usuario_UserId(userId);

        // 3. Obtener promedio global
        Double promedio = calificacionRepository.obtenerPromedioGlobalDelProveedor(userId);
        double promedioFinal = (promedio != null) ? promedio : 0.0;

        // 4. Construir DTO
        return EstadisticasProviderDTO.builder()
                .totalContrataciones(totalContrataciones)
                .totalCalificaciones(totalCalificaciones)
                .promedioGeneral(promedioFinal)
                .build();
    }
}