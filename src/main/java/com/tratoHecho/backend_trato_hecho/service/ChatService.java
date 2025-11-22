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

    // 1. Enviar Mensaje (Crea la sala si no existe)
    @Transactional
    public MensajeResponseDTO sendMessage(MensajeRequestDTO request) {
        Conversacion conversacion;

        // A. Si no viene ID de conversación, buscamos si existe o creamos una
        if (request.getConId() == null || request.getConId() == 0) {
            Optional<Conversacion> existing = conversacionRepository.findExistingConversation(request.getSenderId(), request.getReceiverId());
            if (existing.isPresent()) {
                conversacion = existing.get();
            } else {
                // CREAR NUEVA CONVERSACIÓN
                conversacion = new Conversacion();
                conversacion.setConFechaCreacion(LocalDateTime.now());
                conversacion.setConFechaUltimoMensaje(LocalDateTime.now());
                conversacion = conversacionRepository.save(conversacion);

                // VINCULAR A LOS DOS USUARIOS
                Usuario sender = usuarioRepository.findById(request.getSenderId()).orElseThrow();
                Usuario receiver = usuarioRepository.findById(request.getReceiverId()).orElseThrow();

                createConversacionUsuario(conversacion, sender);
                createConversacionUsuario(conversacion, receiver);
            }
        } else {
            conversacion = conversacionRepository.findById(request.getConId()).orElseThrow();
        }

        // B. Crear y Guardar el Mensaje
        Usuario sender = usuarioRepository.findById(request.getSenderId()).orElseThrow();
        
        Mensaje mensaje = new Mensaje();
        mensaje.setConversacion(conversacion);
        mensaje.setUsuario(sender);
        mensaje.setMsjContenido(request.getContenido());
        mensaje.setMsjFechaEnvio(LocalDateTime.now());
        
        mensajeRepository.save(mensaje);

        // C. Actualizar fecha de la conversación (para que suba en la lista)
        conversacion.setConFechaUltimoMensaje(LocalDateTime.now());
        conversacionRepository.save(conversacion);

        // D. Actualizar "Última Visita" SOLO del que envía (ya que él acaba de ver el chat al escribir)
        updateLastVisit(request.getSenderId(), conversacion.getConId());

        return MensajeResponseDTO.builder()
                .msjId(mensaje.getMsjId())
                .contenido(mensaje.getMsjContenido())
                .fechaEnvio(mensaje.getMsjFechaEnvio())
                .senderId(sender.getUserId())
                .senderName(sender.getUserNombreCompleto())
                .build();
    }

    private void createConversacionUsuario(Conversacion con, Usuario user) {
        ConversacionUsuario cu = new ConversacionUsuario();
        cu.setConversacion(con);
        cu.setUsuario(user);
        cu.setConUserFechaUltimaVisita(LocalDateTime.now()); // Al crearse, ya lo viste
        conversacionUsuarioRepository.save(cu);
    }

    // 2. Obtener Historial de Mensajes de un Chat
    public List<MensajeResponseDTO> getChatHistory(Long conId, Long userId) {
        // Al pedir el historial, asumimos que el usuario está entrando al chat -> Actualizamos visita
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

    // 3. Obtener Inbox (Lista de chats para MessagesScreen)
    public List<InboxDTO> getUserInbox(Long userId) {
        List<ConversacionUsuario> misChats = conversacionUsuarioRepository
                .findByUsuario_UserIdOrderByConversacion_ConFechaUltimoMensajeDesc(userId);

        List<InboxDTO> inboxList = new ArrayList<>();

        for (ConversacionUsuario cu : misChats) {
            Conversacion con = cu.getConversacion();
            
            // Buscar al "Otro" usuario de la conversación
            ConversacionUsuario otroUsuarioCU = con.getUsuarios().stream()
                    .filter(u -> !u.getUsuario().getUserId().equals(userId))
                    .findFirst()
                    .orElse(null);

            if (otroUsuarioCU != null) {
                Usuario otro = otroUsuarioCU.getUsuario();
                
                // Calcular mensajes sin leer para este chat específico
                long unread = con.getMensajes().stream()
                        .filter(m -> m.getMsjFechaEnvio().isAfter(cu.getConUserFechaUltimaVisita()))
                        .count();
                
                // Obtener último mensaje
                String lastMsg = con.getMensajes().stream()
                        .reduce((first, second) -> second) // Obtener el último
                        .map(Mensaje::getMsjContenido)
                        .orElse("Chat iniciado");

                inboxList.add(InboxDTO.builder()
                        .conId(con.getConId())
                        .chatName(otro.getUserNombreCompleto())
                        // .chatImage(otro.getFoto()) // Si tienes foto
                        .lastMessage(lastMsg)
                        .unreadCount((int) unread)
                        .subtitle(otro.getUserNombreUsuario()) // O Rol
                        .rating(4.5) // Puedes calcular esto real luego
                        .build());
            }
        }
        return inboxList;
    }

    // 4. Verificar si hay mensajes sin leer globalmente (Para el MainNavigator)
    public boolean hasUnreadMessagesGlobal(Long userId) {
        Long count = conversacionUsuarioRepository.countUnreadMessages(userId);
        return count > 0;
    }

    public Long getConversationId(Long userId1, Long userId2) {
        Optional<Conversacion> c = conversacionRepository.findExistingConversation(userId1, userId2);
        // Si existe devuelve el ID, si no devuelve null
        return c.map(Conversacion::getConId).orElse(null);
    }

    // Método auxiliar para marcar visto
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