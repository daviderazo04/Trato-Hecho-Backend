package com.tratoHecho.backend_trato_hecho.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CATEGORIA_SERVICIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATSER_ID")
    private Long catSerId;

    @ManyToOne
    @JoinColumn(name = "CAT_ID")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "SER_ID")
    private Servicio servicio;
}
