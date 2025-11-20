package com.tratoHecho.backend_trato_hecho.service;

import com.tratoHecho.backend_trato_hecho.dto.FavoritoRequestDTO;

public interface FavoritoService {
    boolean agregarFavorito(FavoritoRequestDTO favoritoDTO);
    boolean eliminarFavorito(FavoritoRequestDTO favoritoDTO);
    boolean esFavorito(Long userId, Long servicioId);
}