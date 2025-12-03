package com.ues.edu.model;

import java.math.BigDecimal;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "mantenimiento")
@NoArgsConstructor
@AllArgsConstructor
public class Mantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mantenimiento")
    private Long idMantenimiento;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 250, message = "La descripción no debe superar los 250 caracteres")
    @Column(name = "descripcion", nullable = false, length = 250)
    private String descripcion;

    @Size(max = 250, message = "Las observaciones no deben superar los 250 caracteres")
    @Column(name = "observaciones", length = 250)
    private String observaciones;

    @NotNull(message = "Debe seleccionar una bicicleta para el mantenimiento")
    @ManyToOne
    @JoinColumn(name = "id_bicicleta", nullable = false)
    private Bicicleta bicicleta;

    @NotNull(message = "Debe indicar el empleado responsable del mantenimiento")
    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    @NotNull(message = "La fecha del mantenimiento es obligatoria")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_mantenimiento", nullable = false)
    private Date fechaMantenimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoMantenimiento estado;

    @Column(name = "costo", precision = 10, scale = 2)
    private BigDecimal costo;

    @PrePersist
    public void prePersist() {
        if (this.fechaMantenimiento == null) {
            this.fechaMantenimiento = new Date();
        }
        if (this.estado == null) {
            this.estado = EstadoMantenimiento.PENDIENTE;
        }
    }
}
