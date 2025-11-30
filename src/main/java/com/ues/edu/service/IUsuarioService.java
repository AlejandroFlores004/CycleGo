package com.ues.edu.service;

import java.util.List;
import com.ues.edu.model.Usuario;

public interface IUsuarioService {
    Usuario guardar(Usuario usuario);
    Usuario buscarPorUsername(String username);
    List<Usuario> listarTodos();
    Usuario buscarPorId(Long id);
    void eliminar(Long id);
}
