package com.ues.edu.repository;

import com.ues.edu.model.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {
    // aquí puedes agregar consultas custom si las necesitas
}
