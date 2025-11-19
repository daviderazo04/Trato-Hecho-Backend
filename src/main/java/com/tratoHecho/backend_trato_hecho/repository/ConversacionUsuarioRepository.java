package com.tratoHecho.backend_trato_hecho.repository;

import com.tratoHecho.backend_trato_hecho.model.ConversacionUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversacionUsuarioRepository extends JpaRepository<ConversacionUsuario, Long> {

    // Buscar la relación específica de un usuario con una conversación
    Optional<ConversacionUsuario> findByUsuario_UserIdAndConversacion_ConId(Long userId, Long conId);

    // Traer todas las conversaciones de un usuario (para la lista del inbox)
    // Ordenamos por el último mensaje de la conversación
    List<ConversacionUsuario> findByUsuario_UserIdOrderByConversacion_ConFechaUltimoMensajeDesc(Long userId);

    // LA LÓGICA DE LA BURBUJA VERDE GLOBAL
    // Cuenta cuántos mensajes hay en mis conversaciones que sean POSTERIORES a mi última visita
    @Query("SELECT COUNT(m) FROM Mensaje m " +
           "JOIN m.conversacion c " +
           "JOIN c.usuarios cu " +
           "WHERE cu.usuario.userId = :userId " +
           "AND m.msjFechaEnvio > cu.conUserFechaUltimaVisita")
    Long countUnreadMessages(@Param("userId") Long userId);
}