package com.ues.edu.service;

import com.ues.edu.model.Usuario;

public interface IUsuarioService {
    Usuario guardar(Usuario usuario);
    Usuario buscarPorUsername(String username);
}
