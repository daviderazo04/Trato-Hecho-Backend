package com.tratoHecho.backend_trato_hecho.service.impl;

import com.tratoHecho.backend_trato_hecho.dto.ContratarServicioDTO;
import com.tratoHecho.backend_trato_hecho.model.Contratado;
import com.tratoHecho.backend_trato_hecho.model.Servicio;
import com.tratoHecho.backend_trato_hecho.model.Usuario;
import com.tratoHecho.backend_trato_hecho.repository.ContratadoRepository;
import com.tratoHecho.backend_trato_hecho.repository.ServicioRepository;
import com.tratoHecho.backend_trato_hecho.repository.UsuarioRepository;
import com.tratoHecho.backend_trato_hecho.service.ContratadoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ContratadoServiceImpl implements ContratadoService {

    private final ContratadoRepository contratadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;

    public ContratadoServiceImpl(ContratadoRepository contratadoRepository,
                                 UsuarioRepository usuarioRepository,
                                 ServicioRepository servicioRepository) {
        this.contratadoRepository = contratadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicioRepository = servicioRepository;
    }

    @Override
    @Transactional
    public void contratarServicio(ContratarServicioDTO dto) {

        // 1. Validaciones Lógicas de Fechas
        if (dto.getFechaInicio().isAfter(dto.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
        if (dto.getFechaInicio().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No puedes contratar servicios en el pasado.");
        }

        // 2. Validar Disponibilidad (Overlapping en BD)
        boolean ocupado = contratadoRepository.existsByServicioIdAndDateRangeOverlap(
                dto.getServicioId(),
                dto.getFechaInicio(),
                dto.getFechaFin()
        );

        if (ocupado) {
            throw new IllegalArgumentException("El servicio ya está reservado en el horario seleccionado.");
        }

        // 3. Buscar Entidades
        Usuario usuario = usuarioRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Servicio servicio = servicioRepository.findById(dto.getServicioId())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        // 4. Validar estado del servicio
        if (!servicio.getSerEstado()) {
            throw new IllegalStateException("Este servicio no está activo.");
        }

        // 5. Validar auto-contratación
        if (servicio.getUsuario().getUserId().equals(usuario.getUserId())) {
            throw new IllegalArgumentException("No puedes contratar tu propio servicio.");
        }

        // 6. Guardar Contratación
        Contratado contratacion = Contratado.builder()
                .usuario(usuario)
                .servicio(servicio)
                .contrFechaInicio(dto.getFechaInicio())
                .contrFechaFin(dto.getFechaFin())
                .build();

        contratadoRepository.save(contratacion);
    }
}