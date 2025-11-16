package com.tratoHecho.backend_trato_hecho.controller;

import com.tratoHecho.backend_trato_hecho.dto.LoginDTO;
import com.tratoHecho.backend_trato_hecho.dto.RegistroUsuarioDTO;
import com.tratoHecho.backend_trato_hecho.dto.UsuarioLoginResponseDTO;
import com.tratoHecho.backend_trato_hecho.model.Usuario;
import com.tratoHecho.backend_trato_hecho.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Para desarrollo
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    public ResponseEntity<Usuario> registro(@RequestBody RegistroUsuarioDTO registroDTO) {
        Usuario nuevoUsuario = usuarioService.registrar(registroDTO);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        return usuarioService.login(loginDTO)
                .map(usuario -> {
                    // 1. Mapear la Entidad al DTO de Respuesta
                    UsuarioLoginResponseDTO usuarioDTO = UsuarioLoginResponseDTO.builder()
                            .userId(usuario.getUserId())
                            // Aseguramos que el rol esté cargado antes de acceder
                            .rolId(usuario.getRol() != null ? usuario.getRol().getRolId() : null)
                            .userNombreCompleto(usuario.getUserNombreCompleto())
                            .userCorreo(usuario.getUserCorreo())
                            .userGenero(usuario.getUserGenero())
                            .userFechaNacimiento(usuario.getUserFechaNacimiento())
                            .userTelefono(usuario.getUserTelefono())
                            .userNombreUsuario(usuario.getUserNombreUsuario())
                            .userEstadoVerificado(usuario.getUserEstadoVerificado())
                            .userEstado(usuario.getUserEstado())
                            // 2. Incluir las colecciones, que ahora están inicializadas
                            .servicios(usuario.getServicios())
                            .favoritos(usuario.getFavoritos())
                            .conversaciones(usuario.getConversaciones())
                            .mensajesRecibidos(usuario.getMensajesRecibidos())
                            .build();

                    // 3. Devolver la respuesta con el DTO
                    Map<String, Object> response = new HashMap<>();
                    response.put("usuario", usuarioDTO);
                    response.put("mensaje", "Login exitoso");
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("mensaje", "Credenciales inválidas");
                    return ResponseEntity.badRequest().body(response);
                });
    }
}
