package com.ues.edu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ues.edu.dtos.IAlquilerReporteDTO;
import com.ues.edu.model.Alquiler;
import com.ues.edu.model.EstadoAlquiler;

@Repository
public interface AlquilerRepository extends JpaRepository<Alquiler, Long> {
    List<Alquiler> findByEstado(EstadoAlquiler estado);
    @Query("""
           SELECT a.idAlquiler AS idAlquiler,
                  CONCAT(a.cliente.nombres, ' ', a.cliente.apellidos) AS nombreCliente,
                  a.bicicleta.codigo AS codigoBicicleta,
                  a.fechaAlquiler AS fechaAlquiler,
                  a.fechaDevolucionEstimada AS fechaDevolucionEstimada,
                  a.fechaDevolucionReal AS fechaDevolucionReal,
                  CAST(a.estado AS string) AS estado,
                  p.montoTotal AS montoTotal,
                  CAST(p.metodoPago AS string) AS metodoPago
           FROM Alquiler a
           LEFT JOIN a.pagoAlquiler p
           """)
    List<IAlquilerReporteDTO> findDatosReporteAlquileres();
}
