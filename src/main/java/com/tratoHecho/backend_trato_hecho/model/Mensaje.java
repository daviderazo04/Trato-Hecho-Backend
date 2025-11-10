package com.tratoHecho.backend_trato_hecho.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MENSAJE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MSJ_ID")
    private Long msjId;

    @ManyToOne
    @JoinColumn(name = "CON_ID")
    private Conversacion conversacion;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private Usuario usuario;

    @Column(name = "MSJ_IDEMISOR")
    private Integer msjIdEmisor;

    @Column(name = "MSJ_CONTENIDO", columnDefinition = "TEXT")
    private String msjContenido;

    @Column(name = "MSJ_FECHAENVIO")
    private LocalDateTime msjFechaEnvio;
}
