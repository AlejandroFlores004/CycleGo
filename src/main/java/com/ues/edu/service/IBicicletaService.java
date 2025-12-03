package com.ues.edu.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ues.edu.model.Bicicleta;

public interface IBicicletaService {

    Page<Bicicleta> listar(Pageable pageable);

    Optional<Bicicleta> buscarPorId(Integer id);

    void guardar(Bicicleta bicicleta);

    void eliminar(Integer id);
}
