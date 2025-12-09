package com.tratoHecho.backend_trato_hecho.service.impl;

import com.tratoHecho.backend_trato_hecho.dto.ContratarServicioDTO;
import com.tratoHecho.backend_trato_hecho.dto.ContratoDetalleDTO;
import com.tratoHecho.backend_trato_hecho.dto.HistorialTransaccionesDTO;
import com.tratoHecho.backend_trato_hecho.dto.ServicioResponseDTO;
import com.tratoHecho.backend_trato_hecho.model.Calificacion;
import com.tratoHecho.backend_trato_hecho.model.Contratado;
import com.tratoHecho.backend_trato_hecho.model.Servicio;
import com.tratoHecho.backend_trato_hecho.model.Usuario;
import com.tratoHecho.backend_trato_hecho.repository.CalificacionRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContratadoServiceImpl implements ContratadoService {

    private final ContratadoRepository contratadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;

    // Inyectamos CalificacionRepository para buscar las notas
    private final CalificacionRepository calificacionRepository;

    private final ServicioService servicioService;

    public ContratadoServiceImpl(ContratadoRepository contratadoRepository,
                                 UsuarioRepository usuarioRepository,
                                 ServicioRepository servicioRepository,
                                 CalificacionRepository calificacionRepository,
                                 @Lazy ServicioService servicioService) {
        this.contratadoRepository = contratadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicioRepository = servicioRepository;
        this.calificacionRepository = calificacionRepository;
        this.servicioService = servicioService;
    }

    @Override
    @Transactional
    public void contratarServicio(ContratarServicioDTO dto) {
        if (dto.getFechaInicio().isAfter(dto.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
        if (dto.getFechaInicio().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No puedes contratar servicios en el pasado.");
        }

        boolean ocupado = contratadoRepository.existsByServicioIdAndDateRangeOverlap(
                dto.getServicioId(),
                dto.getFechaInicio(),
                dto.getFechaFin()
        );

        if (ocupado) {
            throw new IllegalArgumentException("El servicio ya está reservado en el horario seleccionado.");
        }

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

        // A. Obtener COMPRAS (Yo soy el cliente)
        List<Contratado> misCompras = contratadoRepository.findByUsuario_UserIdOrderByContrFechaInicioDesc(userId);

        List<ContratoDetalleDTO> comprasDTO = misCompras.stream().map(c -> {
            Usuario vendedor = c.getServicio().getUsuario();
            ServicioResponseDTO servicioDTO = servicioService.obtenerServicioDTOPorId(c.getServicio().getSerId(), userId);

            // --- LÓGICA DE CALIFICACIÓN (COMPRA) ---
            // Verificamos si YO (userId) ya califiqué este servicio
            Optional<Calificacion> miCalificacion = calificacionRepository.findByUsuario_UserIdAndServicio_SerId(userId, c.getServicio().getSerId());
            boolean yaCalificado = miCalificacion.isPresent();
            Integer miNota = miCalificacion.map(Calificacion::getCalNota).orElse(null);

            return ContratoDetalleDTO.builder()
                    .contratoId(c.getContrId())
                    .fechaInicio(c.getContrFechaInicio())
                    .fechaFin(c.getContrFechaFin())
                    .contraparteId(vendedor.getUserId())
                    .contraparteNombre(vendedor.getUserNombreCompleto())
                    .contraparteFoto(vendedor.getUserFotoPerfil())
                    .contraparteRolEnTransaccion("VENDEDOR")
                    .servicio(servicioDTO)
                    // Nuevos campos
                    .yaCalificado(yaCalificado)
                    .miNota(miNota)
                    .build();
        }).collect(Collectors.toList());

        // B. Obtener VENTAS (Yo soy el proveedor)
        List<Contratado> misVentas = contratadoRepository.findByServicio_Usuario_UserIdOrderByContrFechaInicioDesc(userId);

        List<ContratoDetalleDTO> ventasDTO = misVentas.stream().map(c -> {
            Usuario comprador = c.getUsuario();
            ServicioResponseDTO servicioDTO = servicioService.obtenerServicioDTOPorId(c.getServicio().getSerId(), userId);

            // --- LÓGICA DE CALIFICACIÓN (VENTA) ---
            // Verificamos si el COMPRADOR (contraparte) ya calificó mi servicio
            Optional<Calificacion> calificacionCliente = calificacionRepository.findByUsuario_UserIdAndServicio_SerId(comprador.getUserId(), c.getServicio().getSerId());
            boolean yaCalificado = calificacionCliente.isPresent();
            Integer notaCliente = calificacionCliente.map(Calificacion::getCalNota).orElse(null);

            return ContratoDetalleDTO.builder()
                    .contratoId(c.getContrId())
                    .fechaInicio(c.getContrFechaInicio())
                    .fechaFin(c.getContrFechaFin())
                    .contraparteId(comprador.getUserId())
                    .contraparteNombre(comprador.getUserNombreCompleto())
                    .contraparteFoto(comprador.getUserFotoPerfil())
                    .contraparteRolEnTransaccion("COMPRADOR")
                    .servicio(servicioDTO)
                    // Nuevos campos (indican si el cliente ya me calificó)
                    .yaCalificado(yaCalificado)
                    .miNota(notaCliente)
                    .build();
        }).collect(Collectors.toList());

        return HistorialTransaccionesDTO.builder()
                .compras(comprasDTO)
                .ventas(ventasDTO)
                .build();
    }
}