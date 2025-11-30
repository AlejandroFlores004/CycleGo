package com.ues.edu.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ues.edu.model.Usuario;
import com.ues.edu.service.IUsuarioService;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final IUsuarioService usuarioService;
    
    public UsuarioDetailsService(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) 
            throws UsernameNotFoundException {

        Usuario usuario = usuarioService.buscarPorUsername(username);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getUsername())
                .password(usuario.getPassword()) // ya viene encriptada
                .roles(usuario.getRol().name())
                .accountLocked(!usuario.isActivo())
                .build();
    }
}
