package com.tratoHecho.backend_trato_hecho.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("servicios", "servicio_detalle");

        cacheManager.setCaffeine(Caffeine.newBuilder()
                // Expira el registro 30 minutos después de haber sido escrito/actualizado
                // Esto significa que cada 30 min, Spring borrará la memoria y volverá a consultar la BD
                .expireAfterWrite(30, TimeUnit.MINUTES)

                // Tamaño máximo de registros en memoria (ej. 500 servicios en detalle)
                // Si llega al límite, borrará los menos usados para hacer espacio
                .maximumSize(500)
                .recordStats());

        return cacheManager;
    }
}