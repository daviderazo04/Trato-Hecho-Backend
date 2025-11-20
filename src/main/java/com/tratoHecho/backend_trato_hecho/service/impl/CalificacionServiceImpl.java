package com.tratoHecho.backend_trato_hecho.service.impl;

import com.tratoHecho.backend_trato_hecho.dto.CalificacionRequestDTO;
import com.tratoHecho.backend_trato_hecho.model.Calificacion;
import com.tratoHecho.backend_trato_hecho.model.Servicio;
import com.tratoHecho.backend_trato_hecho.repository.CalificacionRepository;
import com.tratoHecho.backend_trato_hecho.repository.ServicioRepository;
import com.tratoHecho.backend_trato_hecho.service.CalificacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CalificacionServiceImpl implements CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final ServicioRepository servicioRepository;

    public CalificacionServiceImpl(CalificacionRepository calificacionRepository, ServicioRepository servicioRepository) {
        this.calificacionRepository = calificacionRepository;
        this.servicioRepository = servicioRepository;
    }

    @Override
    @Transactional
    public void agregarCalificacion(CalificacionRequestDTO calificacionDTO) {
        // 1. Validar nota (1 a 5)
        if (calificacionDTO.getNota() < 1 || calificacionDTO.getNota() > 5) {
            throw new IllegalArgumentException("La nota debe estar entre 1 y 5");
        }

        // 2. Buscar Servicio
        Servicio servicio = servicioRepository.findById(calificacionDTO.getServicioId())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        // 3. Crear Calificación
        Calificacion calificacion = Calificacion.builder()
                .servicio(servicio)
                .calNota(calificacionDTO.getNota())
                .build();

        calificacionRepository.save(calificacion);
    }

    @Override
    public Double obtenerPromedio(Long servicioId) {
        Double promedio = calificacionRepository.obtenerPromedioPorServicio(servicioId);
        // Si no hay calificaciones, devuelve null en SQL, así que retornamos 0.0
        return (promedio != null) ? promedio : 0.0;
    }
}