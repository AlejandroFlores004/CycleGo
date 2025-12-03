package com.ues.edu.service;

import com.ues.edu.model.Mantenimiento;

import java.util.List;
import java.util.Optional;

public interface IMantenimientoService {
    List<Mantenimiento> findAll();
    Optional<Mantenimiento> findById(Long id);
    Mantenimiento save(Mantenimiento mantenimiento);
    void deleteById(Long id);
}
