package com.ues.edu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ues.edu.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("""
        SELECT c
        FROM Cliente c
        WHERE e.estado = true
          AND e.idCliente NOT IN (
               SELECT u.cliente.idCliente FROM Usuario u
          )
    """)
    List<Cliente> findClientesActivosSinUsuario();
}
