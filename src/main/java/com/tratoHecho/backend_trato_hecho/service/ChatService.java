package com.tratoHecho.backend_trato_hecho.service;

import com.tratoHecho.backend_trato_hecho.dto.InboxDTO;
import com.tratoHecho.backend_trato_hecho.dto.MensajeRequestDTO;
import com.tratoHecho.backend_trato_hecho.dto.MensajeResponseDTO;
import com.tratoHecho.backend_trato_hecho.model.*;
import com.tratoHecho.backend_trato_hecho.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversacionRepository conversacionRepository;
    private final ConversacionUsuarioRepository conversacionUsuarioRepository;
    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;

    // 1. Enviar Mensaje
    @Transactional
    public MensajeResponseDTO sendMessage(MensajeRequestDTO request) {
        Conversacion conversacion;

        // Si es un chat nuevo
        if (request.getConId() == null || request.getConId() == 0) {
            Optional<Conversacion> existing = Optional.empty();
            
            if (request.getSerId() != null) {
                existing = conversacionRepository.findByUsersAndService(
                        request.getSenderId(), request.getReceiverId(), request.getSerId());
            }

            if (existing.isPresent()) {
                conversacion = existing.get();
            } else {
                // CREAR NUEVA CONVERSACIÓN
                conversacion = new Conversacion();
                conversacion.setConFechaCreacion(LocalDateTime.now());
                conversacion.setConFechaUltimoMensaje(LocalDateTime.now());
                
                if (request.getSerId() != null) {
                    Servicio servicio = servicioRepository.findById(request.getSerId()).orElse(null);
                    conversacion.setServicio(servicio);
                }

                conversacion = conversacionRepository.save(conversacion);

                Usuario sender = usuarioRepository.findById(request.getSenderId()).orElseThrow();
                Usuario receiver = usuarioRepository.findById(request.getReceiverId()).orElseThrow();

                // --- CORRECCIÓN AQUÍ ---
                // El que envía (sender) YA vio el chat: Fecha = Ahora
                createConversacionUsuario(conversacion, sender, LocalDateTime.now());
                
                // El que recibe (receiver) NO lo ha visto: Fecha = Pasado (año 2000)
                // Esto garantiza que el primer mensaje sea posterior a su "última visita"
                createConversacionUsuario(conversacion, receiver, LocalDateTime.of(2000, 1, 1, 0, 0));
                // -----------------------
            }
        } else {
            conversacion = conversacionRepository.findById(request.getConId()).orElseThrow();
        }

        Usuario sender = usuarioRepository.findById(request.getSenderId()).orElseThrow();
        Mensaje mensaje = new Mensaje();
        mensaje.setConversacion(conversacion);
        mensaje.setUsuario(sender);
        mensaje.setMsjContenido(request.getContenido());
        mensaje.setMsjFechaEnvio(LocalDateTime.now());
        mensajeRepository.save(mensaje);

        conversacion.setConFechaUltimoMensaje(LocalDateTime.now());
        conversacionRepository.save(conversacion);
        
        // Actualizamos la visita solo del que envía
        updateLastVisit(request.getSenderId(), conversacion.getConId());

        return MensajeResponseDTO.builder()
                .msjId(mensaje.getMsjId())
                .contenido(mensaje.getMsjContenido())
                .fechaEnvio(mensaje.getMsjFechaEnvio())
                .senderId(sender.getUserId())
                .senderName(sender.getUserNombreCompleto())
                .build();
    }
    
    // Método actualizado para recibir la fecha específica
    private void createConversacionUsuario(Conversacion con, Usuario user, LocalDateTime fechaVisita) {
        ConversacionUsuario cu = new ConversacionUsuario();
        cu.setConversacion(con);
        cu.setUsuario(user);
        cu.setConUserFechaUltimaVisita(fechaVisita); 
        conversacionUsuarioRepository.save(cu);
    }

    // ... (Resto de métodos igual) ...
    public List<InboxDTO> getUserInbox(Long userId) {
        List<ConversacionUsuario> misChats = conversacionUsuarioRepository
                .findByUsuario_UserIdOrderByConversacion_ConFechaUltimoMensajeDesc(userId);

        List<InboxDTO> inboxList = new ArrayList<>();

        for (ConversacionUsuario cu : misChats) {
            Conversacion con = cu.getConversacion();
            
            ConversacionUsuario otroUsuarioCU = con.getUsuarios().stream()
                    .filter(u -> !u.getUsuario().getUserId().equals(userId))
                    .findFirst()
                    .orElse(null);

            if (otroUsuarioCU != null) {
                Usuario otro = otroUsuarioCU.getUsuario();
                Servicio servicio = con.getServicio(); 

                String displayName;
                String displaySubtitle;
                String displayImage;

                if (servicio != null) {
                    displayName = servicio.getSerNombre();
                    displaySubtitle = otro.getUserNombreCompleto(); 
                    
                    if (servicio.getMultimedia() != null && !servicio.getMultimedia().isEmpty()) {
                        displayImage = servicio.getMultimedia().iterator().next().getSerMulLink();
                    } else {
                        displayImage = otro.getUserFotoPerfil(); 
                    }
                } else {
                    displayName = otro.getUserNombreCompleto();
                    displaySubtitle = "Chat Directo"; 
                    displayImage = otro.getUserFotoPerfil();
                }

                long unread = con.getMensajes().stream()
                        .filter(m -> m.getMsjFechaEnvio().isAfter(cu.getConUserFechaUltimaVisita()))
                        .count();
                
                String lastMsg = con.getMensajes().stream()
                        .reduce((first, second) -> second)
                        .map(Mensaje::getMsjContenido)
                        .orElse("Chat iniciado");

                inboxList.add(InboxDTO.builder()
                        .conId(con.getConId())
                        .chatName(displayName)      
                        .chatImage(displayImage)    
                        .subtitle(displaySubtitle)  
                        .lastMessage(lastMsg)
                        .unreadCount((int) unread)
                        .rating(4.5) 
                        .serviceId(servicio != null ? servicio.getSerId() : null)
                        .build());
            }
        }
        return inboxList;
    }

    public Long getConversationId(Long userId1, Long userId2, Long serId) {
        if (serId != null && serId > 0) {
            Optional<Conversacion> c = conversacionRepository.findByUsersAndService(userId1, userId2, serId);
            return c.map(Conversacion::getConId).orElse(null);
        }
        return null; 
    }
    
    public List<MensajeResponseDTO> getChatHistory(Long conId, Long userId) {
        updateLastVisit(userId, conId);
        List<Mensaje> mensajes = mensajeRepository.findByConversacion_ConIdOrderByMsjFechaEnvioAsc(conId);
        return mensajes.stream().map(m -> MensajeResponseDTO.builder()
                .msjId(m.getMsjId())
                .contenido(m.getMsjContenido())
                .fechaEnvio(m.getMsjFechaEnvio())
                .senderId(m.getUsuario().getUserId())
                .senderName(m.getUsuario().getUserNombreCompleto())
                .build()).toList();
    }

    public boolean hasUnreadMessagesGlobal(Long userId) {
        Long count = conversacionUsuarioRepository.countUnreadMessages(userId);
        return count > 0;
    }

    public void updateLastVisit(Long userId, Long conId) {
        Optional<ConversacionUsuario> cuOpt = conversacionUsuarioRepository
                .findByUsuario_UserIdAndConversacion_ConId(userId, conId);
        if (cuOpt.isPresent()) {
            ConversacionUsuario cu = cuOpt.get();
            cu.setConUserFechaUltimaVisita(LocalDateTime.now());
            conversacionUsuarioRepository.save(cu);
        }
    }
}