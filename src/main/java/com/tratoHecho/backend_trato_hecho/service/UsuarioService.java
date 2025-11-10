package com.tratoHecho.backend_trato_hecho.service;

import com.tratoHecho.backend_trato_hecho.dto.LoginDTO;
import com.tratoHecho.backend_trato_hecho.dto.RegistroUsuarioDTO;
import com.tratoHecho.backend_trato_hecho.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<Usuario> findAll();
    Optional<Usuario> findById(Long id);
    Usuario save(Usuario usuario);
    void deleteById(Long id);

    // Métodos nuevos para registro y login
    Usuario registrar(RegistroUsuarioDTO registroDTO);
    Optional<Usuario> login(LoginDTO loginDTO);
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
}
