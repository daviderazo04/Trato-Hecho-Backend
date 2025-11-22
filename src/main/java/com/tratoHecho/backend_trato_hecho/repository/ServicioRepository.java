package com.tratoHecho.backend_trato_hecho.repository;

import com.tratoHecho.backend_trato_hecho.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    // Para la caché pública (Feed general): Solo activos
    List<Servicio> findBySerEstadoTrue();

    // Para "Mis Servicios": También devuelve SOLO los activos
    // (Los que tengan estado false no aparecerán aquí)
    List<Servicio> findByUsuario_UserIdAndSerEstadoTrue(Long userId);

    // Mantenemos el genérico por si lo necesitas internamente para validaciones
    List<Servicio> findByUsuario_UserId(Long userId);
}
