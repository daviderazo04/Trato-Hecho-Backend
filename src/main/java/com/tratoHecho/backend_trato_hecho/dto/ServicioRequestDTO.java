package com.tratoHecho.backend_trato_hecho.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ServicioRequestDTO {
    private Long userId;                        // ID del usuario que publica
    private String nombre;                      // Nombre del servicio
    private String descripcion;                 // Descripción del servicio
    private BigDecimal precio;                  // Precio
    private Boolean estado;                     // Activo / inactivo
    private List<Long> categoriasIds;           // IDs de categorías
    private List<MultimediaDTO> multimedia;     // URLs y tipo (jpg, mp4, etc.)
}
