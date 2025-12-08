package com.tratoHecho.backend_trato_hecho.service;

import com.tratoHecho.backend_trato_hecho.dto.LoginDTO;
import com.tratoHecho.backend_trato_hecho.dto.RegistroUsuarioDTO;
import com.tratoHecho.backend_trato_hecho.model.Usuario;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<Usuario> findAll();
    Optional<Usuario> findById(Long id);
    Usuario save(Usuario usuario);
    void deleteById(Long id);

    // Actualizado para recibir la foto opcional
    Usuario registrar(RegistroUsuarioDTO registroDTO, MultipartFile foto);

    // Nuevo método para actualizar
    Usuario actualizarUsuario(Long id, RegistroUsuarioDTO registroDTO, MultipartFile foto);

    Optional<Usuario> login(LoginDTO loginDTO);
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
}
