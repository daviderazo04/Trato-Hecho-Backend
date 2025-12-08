package com.tratoHecho.backend_trato_hecho.service.impl;

import com.tratoHecho.backend_trato_hecho.dto.LoginDTO;
import com.tratoHecho.backend_trato_hecho.dto.RegistroUsuarioDTO;
import com.tratoHecho.backend_trato_hecho.model.Usuario;
import com.tratoHecho.backend_trato_hecho.repository.UsuarioRepository;
import com.tratoHecho.backend_trato_hecho.service.FirebaseStorageService; // Importar Firebase
import com.tratoHecho.backend_trato_hecho.service.UsuarioService;
import lombok.RequiredArgsConstructor; // Usamos Lombok para el constructor limpio
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final FirebaseStorageService firebaseStorageService; // Inyectamos Firebase

    private static final String DEFAULT_PHOTO_URL = "https://randomuser.me/api/portraits/lego/1.jpg";

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
    @Transactional
    public Usuario registrar(RegistroUsuarioDTO registroDTO, MultipartFile foto) {

        // 1. VALIDACIÓN: Nombre de usuario único
        if (usuarioRepository.existsByUserNombreUsuario(registroDTO.getNombreUsuario())) {
            // Lanzar excepción que el controlador capturará para enviar el mensaje
            throw new IllegalArgumentException("El nombre de usuario '" + registroDTO.getNombreUsuario() + "' ya existe. Por favor escoge otro.");
        }

        String urlFoto = DEFAULT_PHOTO_URL;

        // 2. Lógica de Foto (Opcional)
        if (foto != null && !foto.isEmpty()) {
            try {
                urlFoto = firebaseStorageService.uploadFile(foto);
            } catch (IOException e) {
                throw new RuntimeException("Error al subir la foto de perfil: " + e.getMessage());
            }
        }

        // 3. Crear Usuario
        Usuario usuario = Usuario.builder()
                .userNombreCompleto(registroDTO.getNombreCompleto())
                .userCorreo(registroDTO.getCorreo())
                .userGenero(registroDTO.getGenero())
                .userFechaNacimiento(registroDTO.getFechaNacimiento())
                .userTelefono(registroDTO.getTelefono())
                .userNombreUsuario(registroDTO.getNombreUsuario())
                .userContrasenia(registroDTO.getContrasenia())
                .userRol("CLIENTE")
                .userFotoPerfil(urlFoto) // Asignamos la URL (Firebase o Default)
                .userEstadoVerificado(false)
                .userEstado(true)
                .build();

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario actualizarUsuario(Long id, RegistroUsuarioDTO dto, MultipartFile foto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Actualizamos los campos básicos
        usuario.setUserNombreCompleto(dto.getNombreCompleto());
        usuario.setUserCorreo(dto.getCorreo());
        usuario.setUserGenero(dto.getGenero());
        usuario.setUserFechaNacimiento(dto.getFechaNacimiento());
        usuario.setUserTelefono(dto.getTelefono());

        // Validar si quiere cambiar el username y si ya está ocupado por OTRO usuario
        if (!usuario.getUserNombreUsuario().equals(dto.getNombreUsuario()) &&
                usuarioRepository.existsByUserNombreUsuario(dto.getNombreUsuario())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
        }
        usuario.setUserNombreUsuario(dto.getNombreUsuario());

        // Si envía contraseña nueva, la actualizamos (si viene vacía, mantenemos la anterior)
        if (dto.getContrasenia() != null && !dto.getContrasenia().isEmpty()) {
            usuario.setUserContrasenia(dto.getContrasenia());
        }

        // Lógica de Foto en Actualización
        if (foto != null && !foto.isEmpty()) {
            try {
                String nuevaUrl = firebaseStorageService.uploadFile(foto);
                usuario.setUserFotoPerfil(nuevaUrl);
            } catch (IOException e) {
                throw new RuntimeException("Error al actualizar la foto: " + e.getMessage());
            }
        }

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