package com.tratoHecho.backend_trato_hecho.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CALIFICACION")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Calificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CAL_ID")
    private Long calId;

    @ManyToOne
    @JoinColumn(name = "SER_ID")
    private Servicio servicio;

    @Column(name = "CAL_NOTA")
    private Integer calNota;
}
