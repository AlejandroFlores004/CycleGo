package com.ues.edu.service;

import java.util.List;
import java.util.Optional;

import com.ues.edu.model.Cliente;

public interface IClienteService {

    List<Cliente> listarTodos();

    Cliente buscarPorId(Long idCliente);

    Cliente guardar(Cliente cliente);

    void eliminar(Long idCliente);

    Cliente buscarPorEmail(String email);

    Optional<Cliente> buscarPorDui(String dui); 

}
