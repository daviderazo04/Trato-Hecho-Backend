package com.tratoHecho.backend_trato_hecho.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MENSAJE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MSJ_ID")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long msjId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CON_ID")
    @JsonIgnore
    private Conversacion conversacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    @JsonIgnore
    private Usuario usuario;

    @Column(name = "MSJ_CONTENIDO", columnDefinition = "TEXT")
    private String msjContenido;

    @Column(name = "MSJ_FECHAENVIO")
    private LocalDateTime msjFechaEnvio;
}