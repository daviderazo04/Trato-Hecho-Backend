package com.tratoHecho.backend_trato_hecho.repository;

import com.tratoHecho.backend_trato_hecho.model.Contratado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContratadoRepository extends JpaRepository<Contratado, Long> {

    // VALIDACIÓN DE RANGO DE FECHAS
    // Verifica si existe alguna contratación para este servicio que se solape con las fechas dadas.
    // Lógica: Un evento A solapa con B si: (InicioA < FinB) Y (FinA > InicioB)
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Contratado c " +
            "WHERE c.servicio.serId = :serId " +
            "AND c.contrFechaInicio < :fechaFin " +
            "AND c.contrFechaFin > :fechaInicio")
    boolean existsByServicioIdAndDateRangeOverlap(@Param("serId") Long serId,
                                                  @Param("fechaInicio") LocalDateTime fechaInicio,
                                                  @Param("fechaFin") LocalDateTime fechaFin);

    List<Contratado> findByUsuario_UserId(Long userId);

    // 1. COMPRAS: Donde yo soy el cliente (user_id en tabla contratado)
    List<Contratado> findByUsuario_UserIdOrderByContrFechaInicioDesc(Long userId);

    // 2. VENTAS: Donde yo soy el dueño del servicio
    List<Contratado> findByServicio_Usuario_UserIdOrderByContrFechaInicioDesc(Long userId);

    long countByServicio_Usuario_UserId(Long userId);
}