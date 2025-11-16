package com.tratoHecho.backend_trato_hecho.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SERVICIO_MULTIMEDIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class ServicioMultimedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SERMUL_ID")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long serMulId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MUL_ID")
    @JsonIgnore
    private Multimedia multimedia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SER_ID")
    @JsonIgnore
    private Servicio servicio;

    @Column(name = "SERMUL_LINK", columnDefinition = "TEXT")
    private String serMulLink;
}