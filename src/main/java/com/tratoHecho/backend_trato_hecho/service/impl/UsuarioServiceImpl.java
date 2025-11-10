package com.tratoHecho.backend_trato_hecho.service.impl;

import com.tratoHecho.backend_trato_hecho.dto.LoginDTO;
import com.tratoHecho.backend_trato_hecho.dto.RegistroUsuarioDTO;
import com.tratoHecho.backend_trato_hecho.model.Usuario;
import com.tratoHecho.backend_trato_hecho.repository.UsuarioRepository;
import com.tratoHecho.backend_trato_hecho.service.UsuarioService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public Usuario registrar(RegistroUsuarioDTO registroDTO) {
        Usuario usuario = Usuario.builder()
                .userNombreCompleto(registroDTO.getNombreCompleto())
                .userCorreo(registroDTO.getCorreo())
                .userGenero(registroDTO.getGenero())
                .userFechaNacimiento(registroDTO.getFechaNacimiento())
                .userTelefono(registroDTO.getTelefono())
                .userNombreUsuario(registroDTO.getNombreUsuario())
                .userContrasenia(registroDTO.getContrasenia()) // En un caso real, aquí iría la encriptación
                .userEstadoVerificado(false) // Por defecto no verificado
                .userEstado(true) // Por defecto activo
                .build();

        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> login(LoginDTO loginDTO) {
        return findByNombreUsuario(loginDTO.getNombreUsuario())
                .filter(usuario -> usuario.getUserContrasenia().equals(loginDTO.getContrasenia()));
    }

    @Override
    public Optional<Usuario> findByNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getUserNombreUsuario().equals(nombreUsuario))
                .findFirst();
    }
}
