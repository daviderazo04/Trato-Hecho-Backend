package com.tratoHecho.backend_trato_hecho.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "USUARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long userId;

    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROL_ID")
    @JsonIgnore
    private Rol rol;
    */

    @Column(name = "USER_ROL", length = 15)
    private String userRol;

    @Column(name = "USER_FOTOPERFIL", columnDefinition = "TEXT")
    private String userFotoPerfil;

    @Column(name = "USER_NOMBRECOMPLETO", length = 200)
    private String userNombreCompleto;

    @Column(name = "USER_CORREO", length = 100)
    private String userCorreo;

    @Column(name = "USER_GENERO", length = 10)
    private String userGenero;

    @Column(name = "USER_FECHANACIMIENTO")
    private LocalDate userFechaNacimiento;

    @Column(name = "USER_TELEFONO", length = 15)
    private String userTelefono;

    @Column(name = "USER_NOMBREUSUARIO", length = 100)
    private String userNombreUsuario;

    @Column(name = "USER_CONTRASENIA", length = 100)
    private String userContrasenia;

    @Column(name = "USER_ESTADOVERIFICADO")
    private Boolean userEstadoVerificado;

    @Column(name = "USER_ESTADO")
    private Boolean userEstado;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Set<Servicio> servicios;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Set<Favorito> favoritos;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Set<ConversacionUsuario> conversaciones;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Set<Mensaje> mensajesRecibidos;
}