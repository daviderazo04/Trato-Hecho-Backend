package com.tratoHecho.backend_trato_hecho.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    // CLAVE: Al serializar el Usuario dentro de un Favorito, NO serializar sus listas de nuevo
    @JsonIgnoreProperties({"favoritos", "servicios", "conversaciones", "mensajesRecibidos", "rol", "hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SER_ID")
    // CLAVE: Al serializar el Servicio dentro de un Favorito, NO serializar sus listas de nuevo
    @JsonIgnoreProperties({"favoritos", "calificaciones", "usuario", "categorias", "multimedia", "hibernateLazyInitializer", "handler"})
    private Servicio servicio;
}