package com.tratoHecho.backend_trato_hecho.service.impl;

import com.tratoHecho.backend_trato_hecho.dto.CategoriaResponseDTO;
import com.tratoHecho.backend_trato_hecho.model.Categoria;
import com.tratoHecho.backend_trato_hecho.repository.CategoriaRepository;
import com.tratoHecho.backend_trato_hecho.service.CategoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> obtenerTodas() {
        return categoriaRepository.findAll().stream()
                .map(categoria -> CategoriaResponseDTO.builder()
                        .id(categoria.getCatId())
                        .nombre(categoria.getCatNombre())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDTO obtenerPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));

        return CategoriaResponseDTO.builder()
                .id(categoria.getCatId())
                .nombre(categoria.getCatNombre())
                .build();
    }
}