package com.tratoHecho.backend_trato_hecho.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MultimediaDTO {
    private Long mulId;
    private String url;   // Ej: "https://misarchivos.com/foto1.jpg"
}
