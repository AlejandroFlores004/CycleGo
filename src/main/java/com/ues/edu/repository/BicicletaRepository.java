package com.ues.edu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ues.edu.model.Bicicleta;
import com.ues.edu.model.EstadoBicicleta;

@Repository
public interface BicicletaRepository extends JpaRepository<Bicicleta, Long> {

    List<Bicicleta> findByEstado(EstadoBicicleta estado);

}
