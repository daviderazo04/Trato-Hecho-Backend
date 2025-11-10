package com.tratoHecho.backend_trato_hecho.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "CONTRATADO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contratado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONTR_ID")
    private Long contrId;

    @ManyToOne
    @JoinColumn(name = "SER_ID")
    private Servicio servicio;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private Usuario usuario;

    @Column(name = "CONTR_FECHACONTRATADO")
    private LocalDate contrFechaContratado;
}
