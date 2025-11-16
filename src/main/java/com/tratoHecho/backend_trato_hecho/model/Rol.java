package com.tratoHecho.backend_trato_hecho.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "ROL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROL_ID")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long rolId;

    @Column(name = "ROL_NOMBRE", length = 20)
    private String rolNombre;

    @OneToMany(mappedBy = "rol", fetch = FetchType.LAZY)
    private Set<Usuario> usuarios;
}