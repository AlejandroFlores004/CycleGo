package com.ues.edu.service;

import java.util.List;

import com.ues.edu.model.Alquiler;

public interface IAlquilerService {

    List<Alquiler> listarTodos();

    Alquiler buscarPorId(Long idAlquiler);

    Alquiler guardar(Alquiler alquiler);

    void eliminar(Long idAlquiler);

    List<Alquiler> listarActivos();

}
