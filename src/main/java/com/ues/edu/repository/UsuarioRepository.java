package com.ues.edu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ues.edu.model.Rol;
import com.ues.edu.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);
    List<Usuario> findByRolAndActivoTrue(Rol rol);
}
