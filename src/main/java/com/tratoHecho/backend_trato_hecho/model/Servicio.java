package com.tratoHecho.backend_trato_hecho.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "SERVICIO")
@Getter // Usar Getter
@Setter // Usar Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // CLAVE: Solo usar campos explícitos para hashCode/equals
@ToString(onlyExplicitlyIncluded = true) // Evita StackOverflow en logs
public class Servicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SER_ID")
    @EqualsAndHashCode.Include // Incluir solo el ID
    @ToString.Include
    private Long serId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    @JsonIgnore
    private Usuario usuario; // Mantener LAZY por defecto

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
    @Builder.Default // <-- ESTO ARREGLA EL ERROR
    private Set<Calificacion> calificaciones = new HashSet<>();

    @OneToMany(mappedBy = "servicio", fetch = FetchType.LAZY)
    @Builder.Default // <-- ESTO ARREGLA EL ERROR
    private Set<Favorito> favoritos = new HashSet<>();

    @OneToMany(mappedBy = "servicio", fetch = FetchType.LAZY)
    @Builder.Default // <-- ESTO ARREGLA EL ERROR
    private Set<CategoriaServicio> categorias = new HashSet<>();

    @OneToMany(mappedBy = "servicio", fetch = FetchType.LAZY)
    @Builder.Default // <-- ESTO ARREGLA EL ERROR
    private Set<ServicioMultimedia> multimedia = new HashSet<>();
}