package com.tratoHecho.backend_trato_hecho.controller;

import com.tratoHecho.backend_trato_hecho.dto.ContratarServicioDTO;
import com.tratoHecho.backend_trato_hecho.service.ContratadoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/contrataciones")
@CrossOrigin(origins = "*")
public class ContratadoController {

    private final ContratadoService contratadoService;

    public ContratadoController(ContratadoService contratadoService) {
        this.contratadoService = contratadoService;
    }

    @PostMapping("/contratar")
    public ResponseEntity<?> contratarServicio(@RequestBody ContratarServicioDTO contratacionDTO) {
        Map<String, String> response = new HashMap<>();

        try {
            contratadoService.contratarServicio(contratacionDTO);

            response.put("mensaje", "Servicio contratado con éxito");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (RuntimeException e) {
            response.put("mensaje", e.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }
}