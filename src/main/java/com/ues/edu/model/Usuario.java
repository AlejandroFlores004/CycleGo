package com.ues.edu.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "usuario")
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Pattern(
    regexp = "^[A-Za-z0-9._-]{4,50}$",
    message = "El username solo puede contener letras, números, puntos, guiones y guiones bajos"
    )
    @NotBlank(message = "El username es obligatorio")
    @Size(min = 4, max = 50, message = "El username debe tener entre 4 y 50 caracteres")
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Pattern(
        regexp = "^$|(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,100}$",
        message = "La contraseña debe tener al menos una mayúscula, una minúscula y un número"
    )
    @Size(min = 0, max = 100, message = "La contraseña no debe exceder 100 caracteres")
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Transient
    private String passwordConfirm;

    @NotNull(message = "El rol es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 20)
    private Rol rol;

    @OneToOne
    @JoinColumn(name = "id_empleado", nullable = false, unique = true)
    private Empleado empleado;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_creacion", updatable = false)
    @jakarta.validation.constraints.PastOrPresent(message = "La fecha de creación no puede ser futura")
    private Date fechaCreacion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ultimo_inicio_sesion")
    @jakarta.validation.constraints.PastOrPresent(message = "La fecha de último inicio de sesión no puede ser futura")
    private Date ultimoInicioSesion;

    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "id_usuario_creador")
    private Usuario creadoPor;
}
