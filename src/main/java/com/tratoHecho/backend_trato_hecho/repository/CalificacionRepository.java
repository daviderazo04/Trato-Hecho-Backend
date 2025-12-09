package com.tratoHecho.backend_trato_hecho.repository;

import com.tratoHecho.backend_trato_hecho.model.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    boolean existsByUsuario_UserIdAndServicio_SerId(Long userId, Long serId);

    List<Calificacion> findByServicio_SerId(Long serId);

    Optional<Calificacion> findByUsuario_UserIdAndServicio_SerId(Long userId, Long serId);

    @Query("SELECT AVG(c.calNota) FROM Calificacion c WHERE c.servicio.serId = :serId")
    Double obtenerPromedioPorServicio(@Param("serId") Long serId);


    long countByServicio_Usuario_UserId(Long userId);

    @Query("SELECT AVG(c.calNota) FROM Calificacion c WHERE c.servicio.usuario.userId = :userId")
    Double obtenerPromedioGlobalDelProveedor(@Param("userId") Long userId);
}