package com.tratoHecho.backend_trato_hecho.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tratoHecho.backend_trato_hecho.dto.LoginDTO;
import com.tratoHecho.backend_trato_hecho.dto.RegistroUsuarioDTO;
import com.tratoHecho.backend_trato_hecho.dto.UsuarioLoginResponseDTO;
import com.tratoHecho.backend_trato_hecho.model.Usuario;
import com.tratoHecho.backend_trato_hecho.service.UsuarioService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioService usuarioService;
    private final ObjectMapper objectMapper; // Para convertir JSON string a Objeto

    public AuthController(UsuarioService usuarioService, ObjectMapper objectMapper) {
        this.usuarioService = usuarioService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/registro", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> registro(
            @RequestPart("usuario") String usuarioJson, // JSON como texto
            @RequestPart(value = "foto", required = false) MultipartFile foto // Foto opcional
    ) {
        try {
            RegistroUsuarioDTO registroDTO = objectMapper.readValue(usuarioJson, RegistroUsuarioDTO.class);

            Usuario nuevoUsuario = usuarioService.registrar(registroDTO, foto);
            return ResponseEntity.ok(nuevoUsuario);

        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", e.getMessage());
            response.put("error", "true");
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error en el registro: " + e.getMessage());
        }
    }

    @PutMapping(value = "/usuario/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> actualizarPerfil(
            @PathVariable Long id,
            @RequestPart("usuario") String usuarioJson,
            @RequestPart(value = "foto", required = false) MultipartFile foto
    ) {
        try {
            RegistroUsuarioDTO registroDTO = objectMapper.readValue(usuarioJson, RegistroUsuarioDTO.class);
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, registroDTO, foto);

            return ResponseEntity.ok(usuarioActualizado);

        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        try {
            return usuarioService.login(loginDTO)
                    .map(usuario -> {
                        UsuarioLoginResponseDTO usuarioDTO = UsuarioLoginResponseDTO.builder()
                                .userId(usuario.getUserId())
                                .userRol(usuario.getUserRol())
                                .userFotoPerfil(usuario.getUserFotoPerfil())
                                .userNombreCompleto(usuario.getUserNombreCompleto())
                                .userCorreo(usuario.getUserCorreo())
                                .userGenero(usuario.getUserGenero())
                                .userFechaNacimiento(usuario.getUserFechaNacimiento())
                                .userTelefono(usuario.getUserTelefono())
                                .userNombreUsuario(usuario.getUserNombreUsuario())
                                .userEstadoVerificado(usuario.getUserEstadoVerificado())
                                .userEstado(usuario.getUserEstado())
                                .servicios(usuario.getServicios())
                                .favoritos(usuario.getFavoritos())
                                .conversaciones(usuario.getConversaciones())
                                .mensajesRecibidos(usuario.getMensajesRecibidos())
                                .build();

                        Map<String, Object> response = new HashMap<>();
                        response.put("usuario", usuarioDTO);
                        response.put("mensaje", "Login exitoso");
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        // Esto ocurre si el Optional viene vacío (contraseña incorrecta)
                        Map<String, Object> response = new HashMap<>();
                        response.put("mensaje", "Credenciales inválidas"); // Mensaje para contraseña errónea
                        return ResponseEntity.badRequest().body(response);
                    });

        } catch (IllegalArgumentException e) {
            // Esto captura la excepción "El usuario no existe" lanzada desde el servicio
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", e.getMessage()); // Mensaje: "El usuario no existe"
            return ResponseEntity.badRequest().body(response);
        }
    }
}