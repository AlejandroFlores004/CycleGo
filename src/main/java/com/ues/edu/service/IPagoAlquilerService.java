package com.ues.edu.service;

import java.util.List;

import com.ues.edu.model.Alquiler;
import com.ues.edu.model.PagoAlquiler;

public interface IPagoAlquilerService {

    List<PagoAlquiler> listarTodos();

    PagoAlquiler buscarPorId(Long id);

    PagoAlquiler buscarPorAlquiler(Alquiler alquiler);

    void guardar(PagoAlquiler pagoAlquiler);

    void eliminar(Long id);
}
