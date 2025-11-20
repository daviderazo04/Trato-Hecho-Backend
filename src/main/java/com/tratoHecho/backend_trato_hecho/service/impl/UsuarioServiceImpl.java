package com.tratoHecho.backend_trato_hecho.service.impl;

import com.tratoHecho.backend_trato_hecho.dto.LoginDTO;
import com.tratoHecho.backend_trato_hecho.dto.RegistroUsuarioDTO;
import com.tratoHecho.backend_trato_hecho.model.Usuario;
import com.tratoHecho.backend_trato_hecho.repository.UsuarioRepository;
import com.tratoHecho.backend_trato_hecho.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .userContrasenia(registroDTO.getContrasenia())
                .userRol("CLIENTE")
                .userFotoPerfil(null)
                .userEstadoVerificado(false)
                .userEstado(true)
                .build();

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Optional<Usuario> login(LoginDTO loginDTO) {
        Optional<Usuario> optionalUsuario = findByNombreUsuario(loginDTO.getNombreUsuario())
                .filter(usuario -> usuario.getUserContrasenia().equals(loginDTO.getContrasenia()));

        optionalUsuario.ifPresent(this::initializeAllData);

        return optionalUsuario;
    }

    @Override
    public Optional<Usuario> findByNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByUserNombreUsuario(nombreUsuario);
    }

    private void initializeAllData(Usuario usuario) {

        usuario.getServicios().size();
        usuario.getFavoritos().size();
        usuario.getConversaciones().size();
        usuario.getMensajesRecibidos().size();

        usuario.getServicios().forEach(servicio -> {
            servicio.getCalificaciones().size();
            servicio.getCategorias().size();
            servicio.getMultimedia().size();
        });

        usuario.getConversaciones().forEach(cu -> {
            if (cu.getConversacion() != null) {
                cu.getConversacion().getMensajes().size();
            }
        });
    }
}