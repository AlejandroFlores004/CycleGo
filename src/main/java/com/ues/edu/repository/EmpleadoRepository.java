package com.ues.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ues.edu.model.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
}
