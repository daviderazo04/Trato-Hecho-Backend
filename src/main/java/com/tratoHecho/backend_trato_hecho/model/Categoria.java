package com.tratoHecho.backend_trato_hecho.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "CATEGORIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CAT_ID")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long catId;

    @Column(name = "CAT_NOMBRE", length = 80)
    private String catNombre;

    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private Set<CategoriaServicio> categoriaServicios;
}