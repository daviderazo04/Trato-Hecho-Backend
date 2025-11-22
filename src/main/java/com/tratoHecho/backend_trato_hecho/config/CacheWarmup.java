package com.tratoHecho.backend_trato_hecho.config;

import com.tratoHecho.backend_trato_hecho.service.ServicioService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheWarmup {

    private final ServicioService servicioService;
    private static final Logger logger = LoggerFactory.getLogger(CacheWarmup.class);

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("Iniciando caché de Servicios...");
        long inicio = System.currentTimeMillis();

        servicioService.obtenerTodosDTO();

        long fin = System.currentTimeMillis();
        logger.info("Caché de Servicios lista. Tiempo de carga: {} ms", (fin - inicio));
    }
}