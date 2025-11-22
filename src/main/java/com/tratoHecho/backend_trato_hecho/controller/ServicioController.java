package com.tratoHecho.backend_trato_hecho.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tratoHecho.backend_trato_hecho.dto.ServicioRequestDTO;
import com.tratoHecho.backend_trato_hecho.dto.ServicioResponseDTO;
import com.tratoHecho.backend_trato_hecho.service.ServicioService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/mis-servicios/{userId}")
    public ResponseEntity<List<ServicioResponseDTO>> misServicios(@PathVariable Long userId) {
        return ResponseEntity.ok(servicioService.obtenerMisServicios(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarServicio(@PathVariable Long id, @RequestParam Long userId) {
        try {
            servicioService.eliminarServicio(id, userId);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Servicio eliminado correctamente (Estado cambiado a inactivo)");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // No es dueño
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // No existe
        }
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