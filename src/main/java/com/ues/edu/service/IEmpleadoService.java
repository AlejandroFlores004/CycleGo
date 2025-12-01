package com.ues.edu.service;

import java.util.List;
import com.ues.edu.model.Empleado;

public interface IEmpleadoService {
    Empleado guardar(Empleado empleado);
    List<Empleado> listarTodos();
    Empleado buscarPorId(Long id);
    void eliminar(Long id);
    List<Empleado> findEmpleadosActivosSinUsuario();
}