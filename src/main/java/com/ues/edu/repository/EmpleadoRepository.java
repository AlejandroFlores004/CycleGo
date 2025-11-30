package com.ues.edu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ues.edu.model.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    @Query("""
        SELECT e
        FROM Empleado e
        WHERE e.estado = true
          AND e.idEmpleado NOT IN (
               SELECT u.empleado.idEmpleado FROM Usuario u
          )
    """)
    List<Empleado> findEmpleadosActivosSinUsuario();
}