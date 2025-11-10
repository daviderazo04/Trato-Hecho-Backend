package com.tratoHecho.backend_trato_hecho.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "SERVICIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Servicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SER_ID")
    private Long serId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private Usuario usuario;

    @Column(name = "SER_NOMBRE", length = 60)
    private String serNombre;

    @Column(name = "SER_CATEGORIA", length = 30)
    private String serCategoria;

    @Column(name = "SER_PRECIO")
    private BigDecimal serPrecio;

    @Column(name = "SER_DESCRIPCION", columnDefinition = "TEXT")
    private String serDescripcion;

    @Column(name = "SER_ESTADO")
    private Boolean serEstado;

    @OneToMany(mappedBy = "servicio", fetch = FetchType.LAZY)
    private Set<Calificacion> calificaciones;

    @OneToMany(mappedBy = "servicio", fetch = FetchType.LAZY)
    private Set<Favorito> favoritos;

    @OneToMany(mappedBy = "servicio", fetch = FetchType.LAZY)
    private Set<CategoriaServicio> categorias;

    @OneToMany(mappedBy = "servicio", fetch = FetchType.LAZY)
    private Set<ServicioMultimedia> multimedia;
}
