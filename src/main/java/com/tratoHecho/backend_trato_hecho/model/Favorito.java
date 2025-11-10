package com.tratoHecho.backend_trato_hecho.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "FAVORITOS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FAV_ID")
    private Long favId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "SER_ID")
    private Servicio servicio;
}
