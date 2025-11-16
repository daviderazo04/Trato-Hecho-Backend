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
                .userContrasenia(registroDTO.getContrasenia()) // En un caso real, aquí iría la encriptación
                .userEstadoVerificado(false) // Por defecto no verificado
                .userEstado(true) // Por defecto activo
                .build();

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional // CLAVE: Mantiene la sesión de Hibernate abierta
    public Optional<Usuario> login(LoginDTO loginDTO) {
        // Usamos el método findByNombreUsuario (sin la carga pesada)
        Optional<Usuario> optionalUsuario = findByNombreUsuario(loginDTO.getNombreUsuario())
                .filter(usuario -> usuario.getUserContrasenia().equals(loginDTO.getContrasenia()));

        // Si el login es exitoso, forzamos la carga de las colecciones DENTRO de la transacción
        optionalUsuario.ifPresent(this::initializeAllData);

        return optionalUsuario;
    }

    @Override
    public Optional<Usuario> findByNombreUsuario(String nombreUsuario) {
        // Usamos el método corregido del repositorio
        return usuarioRepository.findByUserNombreUsuario(nombreUsuario);
    }

    // Método auxiliar para forzar la carga de la colección (reemplazando la consulta JOIN FETCH)
    private void initializeAllData(Usuario usuario) {
        // Carga de Rol (ManyToOne)
        if (usuario.getRol() != null) {
            usuario.getRol().getRolNombre();
        }

        // Carga de Primer Nivel (Colecciones de Usuario)
        // Usar .size() para forzar la carga de la colección
        usuario.getServicios().size();
        usuario.getFavoritos().size();
        usuario.getConversaciones().size();
        usuario.getMensajesRecibidos().size();

        // Carga de Segundo Nivel (Ej. Colecciones dentro de Servicios)
        usuario.getServicios().forEach(servicio -> {
            servicio.getCalificaciones().size();
            servicio.getCategorias().size();
            servicio.getMultimedia().size();
        });

        // Carga de Segundo Nivel (Ej. Mensajes dentro de Conversaciones)
        usuario.getConversaciones().forEach(cu -> {
            if (cu.getConversacion() != null) {
                cu.getConversacion().getMensajes().size();
            }
        });
        // Si necesitas cargar las entidades completas dentro de Favoritos, Contratado, etc.,
        // aplica un forEach similar.
    }
}
