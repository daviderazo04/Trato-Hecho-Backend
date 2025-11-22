package com.tratoHecho.backend_trato_hecho.service;

import com.tratoHecho.backend_trato_hecho.dto.ServicioRequestDTO;
import com.tratoHecho.backend_trato_hecho.dto.ServicioResponseDTO;
import com.tratoHecho.backend_trato_hecho.model.*;
import com.tratoHecho.backend_trato_hecho.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
    private final CalificacionRepository calificacionRepository;
    private final FavoritoRepository favoritoRepository;

    private final FirebaseStorageService firebaseStorageService;
    private final CacheManager cacheManager;

    // --- CORRECCIÓN CRÍTICA DE CACHÉ ---
    // Inyectamos el servicio a sí mismo para poder llamar a los métodos @Cacheable
    // pasando por el proxy de Spring. Usamos @Lazy para evitar error de ciclo infinito al arrancar.
    @Autowired
    @Lazy
    private ServicioService self;
    // -----------------------------------

    @Transactional
    public ServicioResponseDTO crearServicio(ServicioRequestDTO dto, List<MultipartFile> archivos) {

        if (archivos != null && archivos.size() > 3) {
            throw new IllegalArgumentException("Solo se permite subir un máximo de 3 archivos multimedia por servicio.");
        }

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

        if (dto.getCategoriasIds() != null && !dto.getCategoriasIds().isEmpty()) {
            for (Long catId : dto.getCategoriasIds()) {
                Categoria categoria = categoriaRepository.findById(catId)
                        .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + catId));

                CategoriaServicio cs = CategoriaServicio.builder()
                        .categoria(categoria)
                        .servicio(servicio)
                        .build();

                categoriaServicioRepository.save(cs);
                servicio.getCategorias().add(cs);
            }
        }

        if (archivos != null && !archivos.isEmpty()) {
            for (MultipartFile archivo : archivos) {
                try {
                    String url = firebaseStorageService.uploadFile(archivo);
                    String tipo = archivo.getContentType() != null && archivo.getContentType().startsWith("video") ? "VIDEO" : "IMAGEN";

                    Multimedia multimedia = Multimedia.builder()
                            .mulTipo(tipo)
                            .build();
                    multimediaRepository.save(multimedia);

                    ServicioMultimedia sm = ServicioMultimedia.builder()
                            .servicio(servicio)
                            .multimedia(multimedia)
                            .serMulLink(url)
                            .build();

                    servicioMultimediaRepository.save(sm);
                    servicio.getMultimedia().add(sm);

                } catch (IOException e) {
                    throw new RuntimeException("Error al subir archivo: " + archivo.getOriginalFilename(), e);
                }
            }
        }

        ServicioResponseDTO nuevoDto = mapearAServicioDTO(servicio);
        nuevoDto.setEsFavorito(false);

        actualizarCacheLista(nuevoDto);

        return nuevoDto;
    }

    // --- MÉTODOS PÚBLICOS (Usan "self" para activar la caché) ---

    public ServicioResponseDTO obtenerServicioDTOPorId(Long id, Long userId) {
        // CORRECCIÓN: Usamos self.obtenerServicioDTOPorIdCached en lugar de llamar directo
        ServicioResponseDTO dtoBase = self.obtenerServicioDTOPorIdCached(id);

        if (userId != null) {
            boolean esFavorito = favoritoRepository.existsByUsuario_UserIdAndServicio_SerId(userId, id);
            return dtoBase.toBuilder().esFavorito(esFavorito).build();
        }

        return dtoBase.toBuilder().esFavorito(false).build();
    }

    public List<ServicioResponseDTO> obtenerTodosDTO(Long userId) {
        // CORRECCIÓN: Usamos self.obtenerTodosDTOCached en lugar de llamar directo.
        // Esto obliga a pasar por el proxy y leer la RAM.
        List<ServicioResponseDTO> listaBase = self.obtenerTodosDTOCached();

        if (userId == null) {
            return listaBase.stream()
                    .map(dto -> dto.toBuilder().esFavorito(false).build())
                    .collect(Collectors.toList());
        }

        Set<Long> misFavoritosIds = favoritoRepository.findServicioIdsByUserId(userId);

        return listaBase.stream()
                .map(dto -> dto.toBuilder()
                        .esFavorito(misFavoritosIds.contains(dto.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    // --- MÉTODOS PRIVADOS CACHEADOS ---

    @Transactional(readOnly = true)
    @Cacheable(value = "servicio_detalle", key = "#id")
    public ServicioResponseDTO obtenerServicioDTOPorIdCached(Long id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));
        return mapearAServicioDTO(servicio);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "servicios", key = "'all'")
    public List<ServicioResponseDTO> obtenerTodosDTOCached() {
        return servicioRepository.findAll().stream()
                .map(this::mapearAServicioDTO)
                .collect(Collectors.toList());
    }

    // --- HELPERS ---

    private ServicioResponseDTO mapearAServicioDTO(Servicio s) {
        List<String> categorias = (s.getCategorias() != null) ? s.getCategorias().stream()
                .map(cs -> cs.getCategoria().getCatNombre())
                .collect(Collectors.toList()) : new ArrayList<>();

        List<String> multimedia = (s.getMultimedia() != null) ? s.getMultimedia().stream()
                .map(ServicioMultimedia::getSerMulLink)
                .collect(Collectors.toList()) : new ArrayList<>();

        Double promedio = calificacionRepository.obtenerPromedioPorServicio(s.getSerId());
        double promedioFinal = (promedio != null) ? promedio : 0.0;

        int totalCalificaciones = (s.getCalificaciones() != null) ? s.getCalificaciones().size() : 0;

        return ServicioResponseDTO.builder()
                .id(s.getSerId())
                .nombre(s.getSerNombre())
                .descripcion(s.getSerDescripcion())
                .precio(s.getSerPrecio())
                .estado(s.getSerEstado())
                .usuarioId(s.getUsuario().getUserId())
                .usuarioNombre(s.getUsuario().getUserNombreCompleto())
                .usuarioFoto(s.getUsuario().getUserFotoPerfil())
                .promedioCalificacion(promedioFinal)
                .totalCalificaciones(totalCalificaciones)
                .categorias(categorias)
                .multimediaUrls(multimedia)
                .esFavorito(false)
                .build();
    }

    private void actualizarCacheLista(ServicioResponseDTO nuevoServicio) {
        Cache cache = cacheManager.getCache("servicios");
        if (cache != null) {
            List<ServicioResponseDTO> listaActual = cache.get("all", List.class);
            if (listaActual != null) {
                List<ServicioResponseDTO> listaModificable = new ArrayList<>(listaActual);
                listaModificable.add(nuevoServicio);
                cache.put("all", listaModificable);
            }
        }
        Cache cacheDetalle = cacheManager.getCache("servicio_detalle");
        if (cacheDetalle != null) {
            cacheDetalle.put(nuevoServicio.getId(), nuevoServicio);
        }
    }
}