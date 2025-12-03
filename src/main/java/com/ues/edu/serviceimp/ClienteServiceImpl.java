package com.ues.edu.serviceimp;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ues.edu.model.Cliente;
import com.ues.edu.repository.ClienteRepository;
import com.ues.edu.service.IClienteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteServiceImpl implements IClienteService {

    private final ClienteRepository clienteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long idCliente) {
        return clienteRepository.findById(idCliente).orElse(null);
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public void eliminar(Long idCliente) {
        clienteRepository.deleteById(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email).orElse(null);
    }

     @Override
    public Optional<Cliente> buscarPorDui(String dui) {
        return clienteRepository.findByDui(dui);
    }
}
