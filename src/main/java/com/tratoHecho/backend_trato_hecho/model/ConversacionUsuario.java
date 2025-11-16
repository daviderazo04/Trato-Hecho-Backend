package com.tratoHecho.backend_trato_hecho.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CONVERSACION_USUARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class ConversacionUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONUSER_ID")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long conUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CON_ID")
    @JsonIgnore
    private Conversacion conversacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    @JsonIgnore
    private Usuario usuario;

    @Column(name = "CONUSER_FECHAULTIMAVISITA")
    private LocalDateTime conUserFechaUltimaVisita;
}