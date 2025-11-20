package com.tratoHecho.backend_trato_hecho.controller;

import com.tratoHecho.backend_trato_hecho.dto.FavoritoRequestDTO;
import com.tratoHecho.backend_trato_hecho.service.FavoritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/favoritos")
@CrossOrigin(origins = "*")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    @PostMapping("/agregar")
    public ResponseEntity<?> agregarFavorito(@RequestBody FavoritoRequestDTO favoritoDTO) {
        boolean agregado = favoritoService.agregarFavorito(favoritoDTO);
        Map<String, String> response = new HashMap<>();

        if (agregado) {
            response.put("mensaje", "Servicio agregado a favoritos correctamente");
            return ResponseEntity.ok(response);
        } else {
            response.put("mensaje", "El servicio ya está en favoritos");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/eliminar") // Usamos POST para enviar el cuerpo JSON cómodamente, aunque DELETE también es válido
    public ResponseEntity<?> eliminarFavorito(@RequestBody FavoritoRequestDTO favoritoDTO) {
        boolean eliminado = favoritoService.eliminarFavorito(favoritoDTO);
        Map<String, String> response = new HashMap<>();

        if (eliminado) {
            response.put("mensaje", "Servicio eliminado de favoritos correctamente");
            return ResponseEntity.ok(response);
        } else {
            response.put("mensaje", "El servicio no estaba en favoritos");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/check/{userId}/{servicioId}")
    public ResponseEntity<Boolean> verificarFavorito(@PathVariable Long userId, @PathVariable Long servicioId) {
        return ResponseEntity.ok(favoritoService.esFavorito(userId, servicioId));
    }
}