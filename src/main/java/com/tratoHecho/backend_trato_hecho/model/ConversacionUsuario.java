package com.tratoHecho.backend_trato_hecho.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CONVERSACION_USUARIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversacionUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONUSER_ID")
    private Long conUserId;

    @ManyToOne
    @JoinColumn(name = "CON_ID")
    private Conversacion conversacion;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private Usuario usuario;

    @Column(name = "CONUSER_FECHAULTIMAVISITA")
    private LocalDateTime conUserFechaUltimaVisita;
}
