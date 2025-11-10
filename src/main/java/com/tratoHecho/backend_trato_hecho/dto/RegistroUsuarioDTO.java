package com.tratoHecho.backend_trato_hecho.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegistroUsuarioDTO {
    private String nombreCompleto;
    private String correo;
    private String genero;
    private LocalDate fechaNacimiento;
    private String telefono;
    private String nombreUsuario;
    private String contrasenia;
}
