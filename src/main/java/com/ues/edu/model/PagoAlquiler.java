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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "pago_alquiler")
@NoArgsConstructor
@AllArgsConstructor
public class PagoAlquiler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago_alquiler")
    private Long idPagoAlquiler;

    @OneToOne
    @JoinColumn(name = "id_alquiler", nullable = false, unique = true)
    private Alquiler alquiler;

    @NotNull(message = "El monto total es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero")
    @Digits(integer = 10, fraction = 2, message = "El monto no debe superar 10 dígitos enteros y 2 decimales")
    @Column(name = "monto_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoTotal;

    @NotNull(message = "El método de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 20)
    private MetodoPago metodoPago;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_pago", nullable = false)
    @PastOrPresent(message = "La fecha de pago no puede ser futura")
    private Date fechaPago;

    @Size(max = 50, message = "La referencia no debe superar los 50 caracteres")
    @Column(name = "referencia", length = 50)
    private String referencia;

    @Size(max = 250, message = "Las observaciones no deben superar los 250 caracteres")
    @Column(name = "observaciones", length = 250)
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoPago estado;

    @ManyToOne
    @JoinColumn(name = "id_usuario_registro", nullable = false)
    private Usuario usuarioRegistro;

    @PrePersist
    public void prePersist() {
        if (this.fechaPago == null) {
            this.fechaPago = new Date();
        }
        if (this.estado == null) {
            this.estado = EstadoPago.PENDIENTE;
        }
    }
}
