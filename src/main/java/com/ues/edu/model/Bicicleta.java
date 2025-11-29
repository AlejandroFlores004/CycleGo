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
@Table(name = "bicicleta")
@NoArgsConstructor
@AllArgsConstructor
public class Bicicleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bicicleta")
    private Integer idBicicleta;

    @NotBlank(message = "El código de la bicicleta es obligatorio")
    @Size(max = 20, message = "El código no debe superar los 20 caracteres")
    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 50, message = "La marca no debe superar los 50 caracteres")
    @Column(name = "marca", nullable = false, length = 50)
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 50, message = "El modelo no debe superar los 50 caracteres")
    @Column(name = "modelo", nullable = false, length = 50)
    private String modelo;

    @Size(max = 200, message = "La descripción no debe superar los 200 caracteres")
    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @NotNull(message = "El estado de la bicicleta es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoBicicleta estado;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private Date fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario_registro", nullable = false)
    @NotNull(message = "Debe especificar quién registró la bicicleta")
    private Usuario usuarioRegistro;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = new Date();
        if (this.estado == null) {
            this.estado = EstadoBicicleta.DISPONIBLE;
        }
    }
}
