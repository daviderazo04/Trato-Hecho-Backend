package com.tratoHecho.backend_trato_hecho.repository;

import com.tratoHecho.backend_trato_hecho.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    // Traer mensajes de una conversación ordenados cronológicamente
    List<Mensaje> findByConversacion_ConIdOrderByMsjFechaEnvioAsc(Long conId);
}