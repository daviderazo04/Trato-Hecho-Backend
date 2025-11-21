package com.tratoHecho.backend_trato_hecho.service;

import com.tratoHecho.backend_trato_hecho.dto.ServicioRequestDTO;
import com.tratoHecho.backend_trato_hecho.dto.ServicioResponseDTO;
import com.tratoHecho.backend_trato_hecho.model.*;
import com.tratoHecho.backend_trato_hecho.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicioService {

    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;
    private final CategoriaRepository categoriaRepository;
    private final CategoriaServicioRepository categoriaServicioRepository;
    private final MultimediaRepository multimediaRepository;
    private final ServicioMultimediaRepository servicioMultimediaRepository;
    private final CalificacionRepository calificacionRepository; // <-- NUEVO: Para los promedios

    @Transactional
    public ServicioResponseDTO crearServicio(ServicioRequestDTO dto) {

        Usuario usuario = usuarioRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getUserId()));

        Servicio servicio = Servicio.builder()
                .usuario(usuario)
                .serNombre(dto.getNombre())
                .serDescripcion(dto.getDescripcion())
                .serPrecio(dto.getPrecio())
                .serEstado(dto.getEstado() != null ? dto.getEstado() : true)
                .build();

        servicioRepository.save(servicio);

        // Asociar categorías
        if (dto.getCategoriasIds() != null && !dto.getCategoriasIds().isEmpty()) {
            for (Long catId : dto.getCategoriasIds()) {
                Categoria categoria = categoriaRepository.findById(catId)
                        .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + catId));

                CategoriaServicio cs = CategoriaServicio.builder()
                        .categoria(categoria)
                        .servicio(servicio)
                        .build();

                categoriaServicioRepository.save(cs);
            }
        }

        // Asociar multimedia existente
        if (dto.getMultimedia() != null && !dto.getMultimedia().isEmpty()) {
            dto.getMultimedia().forEach(media -> {
                Multimedia multimedia = multimediaRepository.findById(media.getMulId())
                        .orElseThrow(() -> new RuntimeException("Multimedia no encontrada con ID: " + media.getMulId()));

                ServicioMultimedia sm = ServicioMultimedia.builder()
                        .servicio(servicio)
                        .multimedia(multimedia)
                        .serMulLink(media.getUrl())
                        .build();

                servicioMultimediaRepository.save(sm);
            });
        }

        // Refrescar listas para el retorno
        List<String> categoriasNombres = categoriaServicioRepository.findByServicio(servicio)
                .stream().map(cs -> cs.getCategoria().getCatNombre()).toList();

        List<String> multimediaUrls = servicioMultimediaRepository.findByServicio(servicio)
                .stream().map(ServicioMultimedia::getSerMulLink).toList();

        // Construir respuesta
        return ServicioResponseDTO.builder()
                .id(servicio.getSerId())
                .nombre(servicio.getSerNombre())
                .descripcion(servicio.getSerDescripcion())
                .precio(servicio.getSerPrecio())
                .estado(servicio.getSerEstado())

                // Datos Proveedor
                .usuarioId(usuario.getUserId())
                .usuarioNombre(usuario.getUserNombreCompleto())
                .usuarioFoto(usuario.getUserFotoPerfil()) // <-- Nuevo campo

                // Métricas (recién creado es 0)
                .promedioCalificacion(0.0)
                .totalCalificaciones(0)

                .categorias(categoriasNombres)
                .multimediaUrls(multimediaUrls)
                .build();
    }

    @Transactional(readOnly = true)
    public List<ServicioResponseDTO> obtenerTodosDTO() {
        return servicioRepository.findAll().stream().map(this::mapearAServicioDTO).toList();
    }

    @Transactional(readOnly = true)
    public ServicioResponseDTO obtenerServicioDTOPorId(Long id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));

        return mapearAServicioDTO(servicio);
    }

    public Optional<Servicio> obtenerPorId(Long id) {
        return servicioRepository.findById(id);
    }

    // --- MÉTODO AUXILIAR PARA NO REPETIR CÓDIGO DE MAPEO ---
    private ServicioResponseDTO mapearAServicioDTO(Servicio s) {
        // Obtener categorías
        List<String> categorias = categoriaServicioRepository.findByServicio(s)
                .stream().map(cs -> cs.getCategoria().getCatNombre()).toList();

        // Obtener multimedia
        List<String> multimedia = servicioMultimediaRepository.findByServicio(s)
                .stream().map(ServicioMultimedia::getSerMulLink).toList();

        // Calcular promedio
        Double promedio = calificacionRepository.obtenerPromedioPorServicio(s.getSerId());
        double promedioFinal = (promedio != null) ? promedio : 0.0;

        return ServicioResponseDTO.builder()
                .id(s.getSerId())
                .nombre(s.getSerNombre())
                .descripcion(s.getSerDescripcion())
                .precio(s.getSerPrecio())
                .estado(s.getSerEstado())

                // Datos Proveedor
                .usuarioId(s.getUsuario().getUserId())
                .usuarioNombre(s.getUsuario().getUserNombreCompleto())
                .usuarioFoto(s.getUsuario().getUserFotoPerfil())

                // Métricas
                .promedioCalificacion(promedioFinal)
                .totalCalificaciones(s.getCalificaciones() != null ? s.getCalificaciones().size() : 0)

                .categorias(categorias)
                .multimediaUrls(multimedia)
                .build();
    }
}