package com.tratoHecho.backend_trato_hecho.repository;

import com.tratoHecho.backend_trato_hecho.model.Conversacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversacionRepository extends JpaRepository<Conversacion, Long> {

    // --- NUEVA CONSULTA: Busca conversación por usuarios Y servicio ---
    // Busca un chat donde estén el Usuario 1 Y el Usuario 2 Y que sea sobre el Servicio X
    @Query("SELECT c FROM Conversacion c " +
           "JOIN c.usuarios cu1 " +
           "JOIN c.usuarios cu2 " +
           "WHERE cu1.usuario.userId = :userId1 " +
           "AND cu2.usuario.userId = :userId2 " +
           "AND c.servicio.serId = :serId") 
    Optional<Conversacion> findByUsersAndService(
            @Param("userId1") Long userId1, 
            @Param("userId2") Long userId2,
            @Param("serId") Long serId);
}