package com.tratoHecho.backend_trato_hecho.repository;

import com.tratoHecho.backend_trato_hecho.model.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    Optional<Favorito> findByUsuario_UserIdAndServicio_SerId(Long userId, Long serId);

    boolean existsByUsuario_UserIdAndServicio_SerId(Long userId, Long serId);

    List<Favorito> findAllByUsuario_UserId(Long userId);

    @Query("SELECT f.servicio.serId FROM Favorito f WHERE f.usuario.userId = :userId")
    Set<Long> findServicioIdsByUserId(@Param("userId") Long userId);
}