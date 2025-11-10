package com.tratoHecho.backend_trato_hecho.controller;

import com.tratoHecho.backend_trato_hecho.dto.ServicioRequestDTO;
import com.tratoHecho.backend_trato_hecho.dto.ServicioResponseDTO;
import com.tratoHecho.backend_trato_hecho.model.Servicio;
import com.tratoHecho.backend_trato_hecho.service.ServicioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@CrossOrigin(origins = "*")
public class ServicioController {

    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    /**
     * Crear un nuevo servicio asociado a un usuario, categorías y multimedia
     */
    @PostMapping
    public ResponseEntity<ServicioResponseDTO> crearServicio(@RequestBody ServicioRequestDTO dto) {
        ServicioResponseDTO nuevoServicio = servicioService.crearServicio(dto);
        return ResponseEntity.ok(nuevoServicio);
    }

    /**
     * (Opcional) Obtener todos los servicios creados
     */
    @GetMapping
    public ResponseEntity<List<ServicioResponseDTO>> listarServicios() {
        return ResponseEntity.ok(servicioService.obtenerTodosDTO());
    }

    /**
     * (Opcional) Obtener un servicio por su ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicioService.obtenerPorId(id));
    }
}
