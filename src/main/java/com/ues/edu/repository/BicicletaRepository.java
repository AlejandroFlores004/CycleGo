package com.ues.edu.repository;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ues.edu.model.Bicicleta;
import com.ues.edu.model.EstadoBicicleta;

public interface BicicletaRepository extends JpaRepository<Bicicleta, Integer> {

    List<Bicicleta> findByEstado(EstadoBicicleta estado);

	Optional<Bicicleta> findByCodigo(String codigo);

}
