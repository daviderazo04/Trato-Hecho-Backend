package com.tratoHecho.backend_trato_hecho.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistorialTransaccionesDTO {
    // Rol Usuario: Servicios que yo he comprado/contratado
    private List<ContratoDetalleDTO> compras;

    // Rol Vendedor: Servicios míos que otros han contratado
    private List<ContratoDetalleDTO> ventas;
}