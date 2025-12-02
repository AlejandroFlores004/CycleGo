package com.ues.edu.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ues.edu.model.Alquiler;
import com.ues.edu.model.PagoAlquiler;

public interface PagoAlquilerRepository extends JpaRepository<PagoAlquiler, Long> {

    Optional<PagoAlquiler> findByAlquiler(Alquiler alquiler);

    boolean existsByAlquiler(Alquiler alquiler);



}
