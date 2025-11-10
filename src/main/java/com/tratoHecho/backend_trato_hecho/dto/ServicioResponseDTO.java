package com.tratoHecho.backend_trato_hecho.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServicioResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Boolean estado;
    private String usuarioNombre;
    private List<String> categorias;
    private List<String> multimediaUrls;
}
