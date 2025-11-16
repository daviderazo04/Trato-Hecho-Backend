package com.tratoHecho.backend_trato_hecho.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CALIFICACION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Calificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CAL_ID")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long calId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SER_ID")
    @JsonIgnore
    private Servicio servicio;

    @Column(name = "CAL_NOTA")
    private Integer calNota;
}