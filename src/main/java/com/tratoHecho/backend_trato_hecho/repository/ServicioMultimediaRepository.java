package com.tratoHecho.backend_trato_hecho.repository;

import com.tratoHecho.backend_trato_hecho.model.Servicio;
import com.tratoHecho.backend_trato_hecho.model.ServicioMultimedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioMultimediaRepository extends JpaRepository<ServicioMultimedia, Long> {
    List<ServicioMultimedia> findByServicio(Servicio servicio);
}