package com.tratoHecho.backend_trato_hecho.controller;

import com.tratoHecho.backend_trato_hecho.dto.EstadisticasProviderDTO;
import com.tratoHecho.backend_trato_hecho.service.EstadisticasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estadisticas")
@CrossOrigin(origins = "*")
public class EstadisticasController {

    private final EstadisticasService estadisticasService;

    public EstadisticasController(EstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<EstadisticasProviderDTO> getEstadisticasUsuario(@PathVariable Long userId) {
        return ResponseEntity.ok(estadisticasService.obtenerEstadisticasProveedor(userId));
    }
}