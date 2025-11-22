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
    private final ObjectMapper objectMapper; // Para convertir el JSON string a Objeto

    public ServicioController(ServicioService servicioService, ObjectMapper objectMapper) {
        this.servicioService = servicioService;
        this.objectMapper = objectMapper;
    }

    // POST: Crear servicio con archivos (Multipart)
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> crearServicio(
            @RequestPart("servicio") String servicioDtoString, // JSON como texto
            @RequestPart(value = "files", required = false) List<MultipartFile> files // Archivos
    ) {
        try {
            // Convertir String a DTO
            ServicioRequestDTO dto = objectMapper.readValue(servicioDtoString, ServicioRequestDTO.class);

            // Llamar al servicio
            ServicioResponseDTO nuevoServicio = servicioService.crearServicio(dto, files);

            return ResponseEntity.ok(nuevoServicio);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error al crear servicio: " + e.getMessage());
        }
    }

    // GET ALL: Listar todos los servicios
    // Acepta un parámetro opcional ?userId=1 para saber si son favoritos de ese usuario
    @GetMapping
    public ResponseEntity<List<ServicioResponseDTO>> listarServicios(@RequestParam(required = false) Long userId) {
        // Llama al método del servicio que maneja la personalización
        return ResponseEntity.ok(servicioService.obtenerTodosDTO(userId));
    }

    // GET BY ID: Obtener detalle de un servicio
    // Acepta parámetro opcional ?userId=1 para saber si es favorito
    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> getServiceById(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId
    ) {
        try {
            // Llama al método del servicio que maneja la personalización
            ServicioResponseDTO servicioDTO = servicioService.obtenerServicioDTOPorId(id, userId);
            return ResponseEntity.ok(servicioDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}