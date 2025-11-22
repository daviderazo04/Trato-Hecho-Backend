package com.tratoHecho.backend_trato_hecho.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CONTRATADO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Contratado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONTR_ID")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long contrId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SER_ID")
    @JsonIgnoreProperties({"contrataciones", "calificaciones", "favoritos", "usuario", "categorias", "multimedia", "hibernateLazyInitializer", "handler"})
    private Servicio servicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    @JsonIgnoreProperties({"contrataciones", "favoritos", "servicios", "conversaciones", "mensajesRecibidos", "hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    // --- NUEVOS CAMPOS ---
    @Column(name = "CONTR_FECHAINI")
    private LocalDateTime contrFechaInicio;

    @Column(name = "CONTR_FECHAFIN")
    private LocalDateTime contrFechaFin;
}