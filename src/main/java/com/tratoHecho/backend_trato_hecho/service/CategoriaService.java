package com.tratoHecho.backend_trato_hecho.service;

import com.tratoHecho.backend_trato_hecho.dto.CategoriaResponseDTO;
import java.util.List;

public interface CategoriaService {
    List<CategoriaResponseDTO> obtenerTodas();
    CategoriaResponseDTO obtenerPorId(Long id);
}