package com.tratoHecho.backend_trato_hecho.service.impl;

import com.tratoHecho.backend_trato_hecho.dto.FavoritoRequestDTO;
import com.tratoHecho.backend_trato_hecho.model.Favorito;
import com.tratoHecho.backend_trato_hecho.model.Servicio;
import com.tratoHecho.backend_trato_hecho.model.Usuario;
import com.tratoHecho.backend_trato_hecho.repository.FavoritoRepository;
import com.tratoHecho.backend_trato_hecho.repository.ServicioRepository;
import com.tratoHecho.backend_trato_hecho.repository.UsuarioRepository;
import com.tratoHecho.backend_trato_hecho.service.FavoritoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FavoritoServiceImpl implements FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;

    public FavoritoServiceImpl(FavoritoRepository favoritoRepository, UsuarioRepository usuarioRepository, ServicioRepository servicioRepository) {
        this.favoritoRepository = favoritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicioRepository = servicioRepository;
    }

    @Override
    @Transactional
    public boolean agregarFavorito(FavoritoRequestDTO favoritoDTO) {
        // 1. Verificar si ya existe para evitar duplicados
        if (favoritoRepository.existsByUsuario_UserIdAndServicio_SerId(favoritoDTO.getUserId(), favoritoDTO.getServicioId())) {
            return false; // Ya existe
        }

        // 2. Buscar las entidades Usuario y Servicio
        Usuario usuario = usuarioRepository.findById(favoritoDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Servicio servicio = servicioRepository.findById(favoritoDTO.getServicioId())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        // 3. Crear y guardar
        Favorito favorito = Favorito.builder()
                .usuario(usuario)
                .servicio(servicio)
                .build();

        favoritoRepository.save(favorito);
        return true;
    }

    @Override
    @Transactional
    public boolean eliminarFavorito(FavoritoRequestDTO favoritoDTO) {
        // Buscar el favorito específico
        Optional<Favorito> favoritoOpt = favoritoRepository.findByUsuario_UserIdAndServicio_SerId(
                favoritoDTO.getUserId(),
                favoritoDTO.getServicioId()
        );

        if (favoritoOpt.isPresent()) {
            favoritoRepository.delete(favoritoOpt.get());
            return true; // Eliminado con éxito
        }

        return false; // No existía
    }

    @Override
    public boolean esFavorito(Long userId, Long servicioId) {
        return favoritoRepository.existsByUsuario_UserIdAndServicio_SerId(userId, servicioId);
    }
}