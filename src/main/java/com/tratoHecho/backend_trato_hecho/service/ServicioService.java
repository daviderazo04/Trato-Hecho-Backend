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

@Service
@RequiredArgsConstructor
public class ServicioService {

    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;
    private final CategoriaRepository categoriaRepository;
    private final CategoriaServicioRepository categoriaServicioRepository;
    private final MultimediaRepository multimediaRepository;
    private final ServicioMultimediaRepository servicioMultimediaRepository;

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

        // DTO limpio usando los repositorios directamente
        List<String> categoriasNombres = categoriaServicioRepository.findByServicio(servicio)
                .stream().map(cs -> cs.getCategoria().getCatNombre()).toList();

        List<String> multimediaUrls = servicioMultimediaRepository.findByServicio(servicio)
                .stream().map(ServicioMultimedia::getSerMulLink).toList();

        return ServicioResponseDTO.builder()
                .id(servicio.getSerId())
                .nombre(servicio.getSerNombre())
                .descripcion(servicio.getSerDescripcion())
                .precio(servicio.getSerPrecio())
                .estado(servicio.getSerEstado())
                .usuarioNombre(usuario.getUserNombreCompleto())
                .categorias(categoriasNombres)
                .multimediaUrls(multimediaUrls)
                .build();
    }

    public List<ServicioResponseDTO> obtenerTodosDTO() {
        return servicioRepository.findAll().stream().map(s -> {
            List<String> categorias = categoriaServicioRepository.findByServicio(s)
                    .stream().map(cs -> cs.getCategoria().getCatNombre()).toList();

            List<String> multimedia = servicioMultimediaRepository.findByServicio(s)
                    .stream().map(ServicioMultimedia::getSerMulLink).toList();

            return ServicioResponseDTO.builder()
                    .id(s.getSerId())
                    .nombre(s.getSerNombre())
                    .descripcion(s.getSerDescripcion())
                    .precio(s.getSerPrecio())
                    .usuarioNombre(s.getUsuario().getUserNombreCompleto())
                    .categorias(categorias)
                    .multimediaUrls(multimedia)
                    .build();
        }).toList();
    }

    public Optional<Servicio> obtenerPorId(Long id) {
        return servicioRepository.findById(id);
    }
}
