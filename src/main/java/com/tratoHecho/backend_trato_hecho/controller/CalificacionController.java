package com.tratoHecho.backend_trato_hecho.controller;

import com.tratoHecho.backend_trato_hecho.dto.CalificacionRequestDTO;
import com.tratoHecho.backend_trato_hecho.service.CalificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/calificaciones")
@CrossOrigin(origins = "*")
public class CalificacionController {

    private final CalificacionService calificacionService;

    public CalificacionController(CalificacionService calificacionService) {
        this.calificacionService = calificacionService;
    }

    @PostMapping("/calificar")
    public ResponseEntity<?> calificarServicio(@RequestBody CalificacionRequestDTO calificacionDTO) {
        try {
            calificacionService.agregarCalificacion(calificacionDTO);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Servicio calificado exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/promedio/{servicioId}")
    public ResponseEntity<Double> obtenerPromedio(@PathVariable Long servicioId) {
        return ResponseEntity.ok(calificacionService.obtenerPromedio(servicioId));
    }
}