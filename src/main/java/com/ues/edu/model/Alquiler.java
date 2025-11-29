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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "alquiler")
@NoArgsConstructor
@AllArgsConstructor
public class Alquiler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alquiler")
    private Long idAlquiler;

    @NotNull(message = "El cliente es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @NotNull(message = "La bicicleta es obligatoria")
    @ManyToOne
    @JoinColumn(name = "id_bicicleta", nullable = false)
    private Bicicleta bicicleta;

    @NotNull(message = "El usuario de registro es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_usuario_registro", nullable = false)
    private Usuario usuarioRegistro;

    @NotNull(message = "La fecha de alquiler es obligatoria")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_alquiler", nullable = false, updatable = false)
    @PastOrPresent(message = "La fecha de alquiler no puede ser futura")
    private Date fechaAlquiler;

    @NotNull(message = "La fecha de devolución estimada es obligatoria")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_devolucion_estimada", nullable = false)
    private Date fechaDevolucionEstimada;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_devolucion_real")
    private Date fechaDevolucionReal;

    @NotNull(message = "El estado del alquiler es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoAlquiler estado;

    @Size(max = 250, message = "Las observaciones no deben superar los 250 caracteres")
    @Column(name = "observaciones", length = 250)
    private String observaciones;

}
