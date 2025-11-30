package com.ues.edu.serviceimp;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ues.edu.model.Usuario;
import com.ues.edu.repository.UsuarioRepository;
import com.ues.edu.service.IUsuarioService;

@Service
@Transactional
public class UsuarioServiceImp implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImp(UsuarioRepository usuarioRepository,
                             PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario guardar(Usuario usuario) {

        // Si es nuevo o la password viene "en claro", la encriptamos
        if (usuario.getIdUsuario() == null) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        } else {
            String pwd = usuario.getPassword();
            if (pwd != null &&
                !pwd.startsWith("$2a$") &&
                !pwd.startsWith("$2b$") &&
                !pwd.startsWith("$2y$")) {
                usuario.setPassword(passwordEncoder.encode(pwd));
            }
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username).orElse(null);
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }
}
