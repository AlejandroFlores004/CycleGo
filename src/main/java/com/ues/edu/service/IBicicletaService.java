package com.ues.edu.service;

import java.util.List;
import com.ues.edu.model.Bicicleta;

public interface IBicicletaService {

    List<Bicicleta> listarTodas();

    Bicicleta buscarPorId(Long idBicicleta);

    Bicicleta guardar(Bicicleta bicicleta);

    void eliminar(Long idBicicleta);
}
