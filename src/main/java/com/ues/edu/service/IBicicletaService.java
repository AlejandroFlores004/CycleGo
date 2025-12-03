package com.ues.edu.service;

import java.util.List;

import com.ues.edu.model.Bicicleta;

public interface IBicicletaService {

    List<Bicicleta> listar();

    Bicicleta buscarPorId(Integer id);

    void guardar(Bicicleta bicicleta);

    void eliminar(Integer id);
}
