package com.tratoHecho.backend_trato_hecho.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "MULTIMEDIA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Multimedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MUL_ID")
    private Long mulId;

    @Column(name = "MUL_TIPO", length = 10)
    private String mulTipo;

    @OneToMany(mappedBy = "multimedia", fetch = FetchType.LAZY)
    private Set<ServicioMultimedia> servicioMultimedia;
}
