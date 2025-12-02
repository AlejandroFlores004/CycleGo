package com.ues.edu.service;

import java.util.List;
import com.ues.edu.model.Cliente;

public interface IClienteService {

    List<Cliente> listarTodos();

    Cliente buscarPorId(Long idCliente);

    Cliente guardar(Cliente cliente);

    void eliminar(Long idCliente);
}
