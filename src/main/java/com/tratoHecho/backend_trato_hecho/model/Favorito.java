package com.tratoHecho.backend_trato_hecho.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "FAVORITOS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Favorito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FAV_ID")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long favId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SER_ID")
    private Servicio servicio;
}