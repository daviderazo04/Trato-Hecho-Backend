package com.tratoHecho.backend_trato_hecho.service;

import com.tratoHecho.backend_trato_hecho.dto.ServicioRequestDTO;
import com.tratoHecho.backend_trato_hecho.dto.ServicioResponseDTO;
import com.tratoHecho.backend_trato_hecho.model.*;
import com.tratoHecho.backend_trato_hecho.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final CalificacionRepository calificacionRepository;

    // Inyectamos el servicio de Firebase
    private final FirebaseStorageService firebaseStorageService;

    @Transactional
    public ServicioResponseDTO crearServicio(ServicioRequestDTO dto, List<MultipartFile> archivos) {

        // 1. Crear el Servicio base (Datos de texto)
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

        // 2. Asociar categorías
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

        // 3. ✅ FLUJO VERIFICADO: SUBIR ARCHIVOS A FIREBASE Y GUARDAR URL
        if (archivos != null && !archivos.isEmpty()) {
            for (MultipartFile archivo : archivos) {
                try {
                    // A. Subir a Firebase y obtener URL pública
                    String url = firebaseStorageService.uploadFile(archivo);

                    // B. Determinar tipo (IMAGEN o VIDEO)
                    String tipo = archivo.getContentType().startsWith("video") ? "VIDEO" : "IMAGEN";

                    // C. Crear registro de tipo Multimedia
                    // (Idealmente aquí buscarías si ya existe el tipo para reutilizarlo, pero crear uno nuevo funciona)
                    Multimedia multimedia = Multimedia.builder()
                            .mulTipo(tipo)
                            .build();
                    multimediaRepository.save(multimedia);

                    // D. Guardar en tabla intermedia SERVICIO_MULTIMEDIA
                    ServicioMultimedia sm = ServicioMultimedia.builder()
                            .servicio(servicio)
                            .multimedia(multimedia)
                            .serMulLink(url) // <-- AQUÍ SE GUARDA EL LINK DE FIREBASE EN LA BD
                            .build();

                    servicioMultimediaRepository.save(sm);

                } catch (IOException e) {
                    throw new RuntimeException("Error al subir archivo: " + archivo.getOriginalFilename(), e);
                }
            }
        }

        // 4. Mapear respuesta para devolver al frontend
        return mapearAServicioDTO(servicio);
    }

    @Transactional(readOnly = true)
    public ServicioResponseDTO obtenerServicioDTOPorId(Long id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));
        return mapearAServicioDTO(servicio);
    }

    @Transactional(readOnly = true)
    public List<ServicioResponseDTO> obtenerTodosDTO() {
        return servicioRepository.findAll().stream().map(this::mapearAServicioDTO).toList();
    }

    private ServicioResponseDTO mapearAServicioDTO(Servicio s) {
        List<String> categorias = categoriaServicioRepository.findByServicio(s)
                .stream().map(cs -> cs.getCategoria().getCatNombre()).toList();

        List<String> multimedia = servicioMultimediaRepository.findByServicio(s)
                .stream().map(ServicioMultimedia::getSerMulLink).toList();

        Double promedio = calificacionRepository.obtenerPromedioPorServicio(s.getSerId());
        double promedioFinal = (promedio != null) ? promedio : 0.0;

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
                .totalCalificaciones(s.getCalificaciones() != null ? s.getCalificaciones().size() : 0)
                .categorias(categorias)
                .multimediaUrls(multimedia)
                .build();
    }
}