package com.tratoHecho.backend_trato_hecho.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "CONVERSACION")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CON_ID")
    private Long conId;

    @Column(name = "CON_FECHACREACION")
    private LocalDateTime conFechaCreacion;

    @Column(name = "CON_FECHAULTIMOMENSAJE")
    private LocalDateTime conFechaUltimoMensaje;

    @OneToMany(mappedBy = "conversacion", fetch = FetchType.LAZY)
    private Set<Mensaje> mensajes;

    @OneToMany(mappedBy = "conversacion", fetch = FetchType.LAZY)
    private Set<ConversacionUsuario> usuarios;
}
