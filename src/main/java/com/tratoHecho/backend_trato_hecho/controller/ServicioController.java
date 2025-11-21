package com.tratoHecho.backend_trato_hecho.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tratoHecho.backend_trato_hecho.dto.ServicioRequestDTO;
import com.tratoHecho.backend_trato_hecho.dto.ServicioResponseDTO;
import com.tratoHecho.backend_trato_hecho.service.ServicioService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@CrossOrigin(origins = "*")
public class ServicioController {

    private final ServicioService servicioService;
    private final ObjectMapper objectMapper; // Necesario para parsear el JSON manualmente

    public ServicioController(ServicioService servicioService, ObjectMapper objectMapper) {
        this.servicioService = servicioService;
        this.objectMapper = objectMapper;
    }

    // Endpoint para CREAR servicio con archivos (Multipart)
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> crearServicio(
            @RequestPart("servicio") String servicioDtoString, // Recibe el JSON como String
            @RequestPart(value = "files", required = false) List<MultipartFile> files // Recibe la lista de archivos
    ) {
        try {
            // 1. Convertir el String JSON al DTO
            ServicioRequestDTO dto = objectMapper.readValue(servicioDtoString, ServicioRequestDTO.class);

            // 2. Llamar al servicio enviando el DTO y los Archivos
            ServicioResponseDTO nuevoServicio = servicioService.crearServicio(dto, files);

            return ResponseEntity.ok(nuevoServicio);
        } catch (Exception e) {
            // Manejo de errores (JSON mal formado, error de subida, etc.)
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error al crear servicio: " + e.getMessage());
        }
    }

    // Endpoint para LISTAR todos los servicios (DTO limpio)
    @GetMapping
    public ResponseEntity<List<ServicioResponseDTO>> listarServicios() {
        return ResponseEntity.ok(servicioService.obtenerTodosDTO());
    }

    // Endpoint para obtener UN servicio por ID (DTO limpio con promedio)
    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> getServiceById(@PathVariable Long id) {
        try {
            ServicioResponseDTO servicioDTO = servicioService.obtenerServicioDTOPorId(id);
            return ResponseEntity.ok(servicioDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}