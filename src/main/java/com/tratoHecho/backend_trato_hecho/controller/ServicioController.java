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
    private final ObjectMapper objectMapper;

    public ServicioController(ServicioService servicioService, ObjectMapper objectMapper) {
        this.servicioService = servicioService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> crearServicio(
            @RequestPart("servicio") String servicioDtoString,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        try {
            ServicioRequestDTO dto = objectMapper.readValue(servicioDtoString, ServicioRequestDTO.class);
            ServicioResponseDTO nuevoServicio = servicioService.crearServicio(dto, files);
            return ResponseEntity.ok(nuevoServicio);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error al crear servicio: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<ServicioResponseDTO>> listarServicios(@RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(servicioService.obtenerTodosDTO(userId));
    }

    @GetMapping("/favoritos/{userId}")
    public ResponseEntity<List<ServicioResponseDTO>> listarSoloFavoritos(@PathVariable Long userId) {
        return ResponseEntity.ok(servicioService.obtenerFavoritosDeUsuario(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> getServiceById(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId
    ) {
        try {
            ServicioResponseDTO servicioDTO = servicioService.obtenerServicioDTOPorId(id, userId);
            return ResponseEntity.ok(servicioDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}