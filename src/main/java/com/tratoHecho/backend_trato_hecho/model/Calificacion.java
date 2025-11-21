package com.tratoHecho.backend_trato_hecho.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @JsonIgnoreProperties({"calificaciones", "favoritos", "usuario", "categorias", "multimedia", "hibernateLazyInitializer", "handler"})
    private Servicio servicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    @JsonIgnoreProperties({"favoritos", "servicios", "conversaciones", "mensajesRecibidos", "hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    @Column(name = "CAL_NOTA")
    private Integer calNota;
}