package com.tratoHecho.backend_trato_hecho.service.impl;

import com.tratoHecho.backend_trato_hecho.dto.CalificacionRequestDTO;
import com.tratoHecho.backend_trato_hecho.model.Calificacion;
import com.tratoHecho.backend_trato_hecho.model.Servicio;
import com.tratoHecho.backend_trato_hecho.model.Usuario;
import com.tratoHecho.backend_trato_hecho.repository.CalificacionRepository;
import com.tratoHecho.backend_trato_hecho.repository.ServicioRepository;
import com.tratoHecho.backend_trato_hecho.repository.UsuarioRepository;
import com.tratoHecho.backend_trato_hecho.service.CalificacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CalificacionServiceImpl implements CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final ServicioRepository servicioRepository;
    private final UsuarioRepository usuarioRepository;

    public CalificacionServiceImpl(CalificacionRepository calificacionRepository,
                                   ServicioRepository servicioRepository,
                                   UsuarioRepository usuarioRepository) {
        this.calificacionRepository = calificacionRepository;
        this.servicioRepository = servicioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public void agregarCalificacion(CalificacionRequestDTO calificacionDTO) {
        if (calificacionDTO.getNota() < 1 || calificacionDTO.getNota() > 5) {
            throw new IllegalArgumentException("La nota debe estar entre 1 y 5");
        }

        if (calificacionRepository.existsByUsuario_UserIdAndServicio_SerId(calificacionDTO.getUserId(), calificacionDTO.getServicioId())) {
            throw new IllegalArgumentException("El usuario ya ha calificado este servicio anteriormente.");
        }

        Usuario usuario = usuarioRepository.findById(calificacionDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + calificacionDTO.getUserId()));

        Servicio servicio = servicioRepository.findById(calificacionDTO.getServicioId())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + calificacionDTO.getServicioId()));

        Calificacion calificacion = Calificacion.builder()
                .usuario(usuario)
                .servicio(servicio)
                .calNota(calificacionDTO.getNota())
                .build();

        calificacionRepository.save(calificacion);
    }

    @Override
    public Double obtenerPromedio(Long servicioId) {
        Double promedio = calificacionRepository.obtenerPromedioPorServicio(servicioId);
        return (promedio != null) ? promedio : -1.0;
    }
}