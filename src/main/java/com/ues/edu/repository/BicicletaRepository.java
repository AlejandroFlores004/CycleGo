package com.ues.edu.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ues.edu.model.Bicicleta;

public interface BicicletaRepository extends JpaRepository<Bicicleta, Integer> {

	Optional<Bicicleta> findByCodigo(String codigo);

}
