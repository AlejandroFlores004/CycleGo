package com.ues.edu.repository;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ues.edu.dtos.IBicicletaReporteDTO;
import com.ues.edu.model.Bicicleta;
import com.ues.edu.model.EstadoBicicleta;

public interface BicicletaRepository extends JpaRepository<Bicicleta, Integer> {

    List<Bicicleta> findByEstado(EstadoBicicleta estado);

	Optional<Bicicleta> findByCodigo(String codigo);

    @Query("""
        SELECT 
            b.idBicicleta AS idBicicleta,
            b.codigo      AS codigo,
            b.marca       AS marca,
            b.modelo      AS modelo,
            b.descripcion AS descripcion,
            str(b.estado) AS estado,
            b.fechaCreacion AS fechaCreacion,
            u.username    AS usuarioRegistro
        FROM Bicicleta b
        JOIN b.usuarioRegistro u
        ORDER BY b.idBicicleta
    """)
    List<IBicicletaReporteDTO> findDatosReporteBicicletas();
}
