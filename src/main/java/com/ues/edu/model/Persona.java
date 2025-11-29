package com.ues.edu.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Persona {
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres no deben superar los 100 caracteres")
    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no deben superar los 100 caracteres")
    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @NotBlank(message = "El DUI es obligatorio")
    @Pattern(regexp = "\\d{8}-\\d", message = "El DUI debe tener el formato 12345678-9")
    @Column(name = "dui", nullable = false, unique = true, length = 10)
    private String dui;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(
        regexp = "^[267][0-9]{7}$",
        message = "El teléfono debe tener 8 dígitos y comenzar con 2, 6 o 7"
    )
    @Column(name = "telefono", nullable = false, length = 8)
    private String telefono;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    @Column(name = "email", nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "estado", nullable = false)
    private boolean estado;

    @PrePersist
    public void prePersist() {
        // si no se setea estado, lo dejamos true por defecto
        if (!this.estado) {
            this.estado = true;
        }
    }
}
