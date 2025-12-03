package com.ues.edu.dtos;

import java.util.Date;

public interface IBicicletaReporteDTO {

    Integer getIdBicicleta();
    String getCodigo();
    String getMarca();
    String getModelo();
    String getDescripcion();
    String getEstado();
    Date getFechaCreacion();
    String getUsuarioRegistro();
}
