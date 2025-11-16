package com.tratoHecho.backend_trato_hecho.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CATEGORIA_SERVICIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class CategoriaServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATSER_ID")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long catSerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CAT_ID")
    @JsonIgnore
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SER_ID")
    @JsonIgnore
    private Servicio servicio;
}