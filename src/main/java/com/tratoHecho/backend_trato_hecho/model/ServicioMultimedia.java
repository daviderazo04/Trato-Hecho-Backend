package com.tratoHecho.backend_trato_hecho.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SERVICIO_MULTIMEDIA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicioMultimedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SERMUL_ID")
    private Long serMulId;

    @ManyToOne
    @JoinColumn(name = "MUL_ID")
    private Multimedia multimedia;

    @ManyToOne
    @JoinColumn(name = "SER_ID")
    private Servicio servicio;

    @Column(name = "SERMUL_LINK", columnDefinition = "TEXT")
    private String serMulLink;
}
