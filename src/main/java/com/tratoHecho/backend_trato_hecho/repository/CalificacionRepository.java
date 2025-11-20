package com.tratoHecho.backend_trato_hecho.repository;

import com.tratoHecho.backend_trato_hecho.model.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    List<Calificacion> findByServicio_SerId(Long serId);

    @Query("SELECT AVG(c.calNota) FROM Calificacion c WHERE c.servicio.serId = :serId")
    Double obtenerPromedioPorServicio(@Param("serId") Long serId);
}