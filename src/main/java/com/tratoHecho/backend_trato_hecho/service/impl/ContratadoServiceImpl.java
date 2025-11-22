package com.tratoHecho.backend_trato_hecho.service.impl;

import com.tratoHecho.backend_trato_hecho.dto.ContratarServicioDTO;
import com.tratoHecho.backend_trato_hecho.dto.ContratoDetalleDTO;
import com.tratoHecho.backend_trato_hecho.dto.HistorialTransaccionesDTO;
import com.tratoHecho.backend_trato_hecho.dto.ServicioResponseDTO;
import com.tratoHecho.backend_trato_hecho.model.Contratado;
import com.tratoHecho.backend_trato_hecho.model.Servicio;
import com.tratoHecho.backend_trato_hecho.model.Usuario;
import com.tratoHecho.backend_trato_hecho.repository.ContratadoRepository;
import com.tratoHecho.backend_trato_hecho.repository.ServicioRepository;
import com.tratoHecho.backend_trato_hecho.repository.UsuarioRepository;
import com.tratoHecho.backend_trato_hecho.service.ContratadoService;
import com.tratoHecho.backend_trato_hecho.service.ServicioService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContratadoServiceImpl implements ContratadoService {

    private final ContratadoRepository contratadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;

    // Inyectamos ServicioService para reutilizar el mapeo DTO completo (con caché y promedios)
    private final ServicioService servicioService;

    public ContratadoServiceImpl(ContratadoRepository contratadoRepository,
                                 UsuarioRepository usuarioRepository,
                                 ServicioRepository servicioRepository,
                                 @Lazy ServicioService servicioService) {
        this.contratadoRepository = contratadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicioRepository = servicioRepository;
        this.servicioService = servicioService;
    }

    @Override
    @Transactional
    public void contratarServicio(ContratarServicioDTO dto) {
        // 1. Validaciones de Fecha
        if (dto.getFechaInicio().isAfter(dto.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
        if (dto.getFechaInicio().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No puedes contratar servicios en el pasado.");
        }

        // 2. Validar Disponibilidad
        boolean ocupado = contratadoRepository.existsByServicioIdAndDateRangeOverlap(
                dto.getServicioId(),
                dto.getFechaInicio(),
                dto.getFechaFin()
        );

        if (ocupado) {
            throw new IllegalArgumentException("El servicio ya está reservado en el horario seleccionado.");
        }

        // 3. Buscar y Validar Entidades
        Usuario usuario = usuarioRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Servicio servicio = servicioRepository.findById(dto.getServicioId())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        if (!servicio.getSerEstado()) {
            throw new IllegalStateException("Este servicio no está activo.");
        }

        if (servicio.getUsuario().getUserId().equals(usuario.getUserId())) {
            throw new IllegalArgumentException("No puedes contratar tu propio servicio.");
        }

        // 4. Guardar
        Contratado contratacion = Contratado.builder()
                .usuario(usuario)
                .servicio(servicio)
                .contrFechaInicio(dto.getFechaInicio())
                .contrFechaFin(dto.getFechaFin())
                .build();

        contratadoRepository.save(contratacion);
    }

    @Override
    @Transactional(readOnly = true)
    public HistorialTransaccionesDTO obtenerHistorialPorUsuario(Long userId) {

        // A. Obtener COMPRAS (Donde soy Cliente)
        List<Contratado> misCompras = contratadoRepository.findByUsuario_UserIdOrderByContrFechaInicioDesc(userId);

        List<ContratoDetalleDTO> comprasDTO = misCompras.stream().map(c -> {
            // La contraparte es el Vendedor (Dueño del servicio)
            Usuario vendedor = c.getServicio().getUsuario();

            // Reutilizamos el servicioService para obtener el DTO del servicio completo y optimizado
            // Pasamos 'userId' para que el campo 'esFavorito' se calcule correctamente para mí
            ServicioResponseDTO servicioDTO = servicioService.obtenerServicioDTOPorId(c.getServicio().getSerId(), userId);

            return ContratoDetalleDTO.builder()
                    .contratoId(c.getContrId())
                    .fechaInicio(c.getContrFechaInicio())
                    .fechaFin(c.getContrFechaFin())
                    .contraparteId(vendedor.getUserId())
                    .contraparteNombre(vendedor.getUserNombreCompleto())
                    .contraparteFoto(vendedor.getUserFotoPerfil())
                    .contraparteRolEnTransaccion("VENDEDOR")
                    .servicio(servicioDTO)
                    .build();
        }).collect(Collectors.toList());

        // B. Obtener VENTAS (Donde soy Vendedor)
        List<Contratado> misVentas = contratadoRepository.findByServicio_Usuario_UserIdOrderByContrFechaInicioDesc(userId);

        List<ContratoDetalleDTO> ventasDTO = misVentas.stream().map(c -> {
            // La contraparte es el Comprador (Cliente)
            Usuario comprador = c.getUsuario();

            // Obtenemos el DTO del servicio.
            // Nota: 'esFavorito' aquí reflejará si YO (vendedor) tengo mi propio servicio en favoritos,
            // lo cual es técnicamente correcto según la lógica de reuse.
            ServicioResponseDTO servicioDTO = servicioService.obtenerServicioDTOPorId(c.getServicio().getSerId(), userId);

            return ContratoDetalleDTO.builder()
                    .contratoId(c.getContrId())
                    .fechaInicio(c.getContrFechaInicio())
                    .fechaFin(c.getContrFechaFin())
                    .contraparteId(comprador.getUserId())
                    .contraparteNombre(comprador.getUserNombreCompleto())
                    .contraparteFoto(comprador.getUserFotoPerfil())
                    .contraparteRolEnTransaccion("COMPRADOR")
                    .servicio(servicioDTO)
                    .build();
        }).collect(Collectors.toList());

        // C. Retornar el objeto agrupado
        return HistorialTransaccionesDTO.builder()
                .compras(comprasDTO)
                .ventas(ventasDTO)
                .build();
    }
}