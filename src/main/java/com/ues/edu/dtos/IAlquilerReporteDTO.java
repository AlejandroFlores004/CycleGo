package com.ues.edu.dtos;

import java.math.BigDecimal;
import java.util.Date;

public interface IAlquilerReporteDTO {

    Long getIdAlquiler();
    String getNombreCliente();
    String getCodigoBicicleta();
    Date getFechaAlquiler();
    Date getFechaDevolucionEstimada();
    Date getFechaDevolucionReal();
    String getEstado();
    BigDecimal getMontoTotal();
    String getMetodoPago();
}
