package com.ues.edu.config;

import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ues.edu.model.Empleado;
import com.ues.edu.model.Rol;
import com.ues.edu.model.Usuario;
import com.ues.edu.repository.EmpleadoRepository;
import com.ues.edu.repository.UsuarioRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initAdminUser(
            UsuarioRepository usuarioRepository,
            EmpleadoRepository empleadoRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {

        return args -> {

            if (usuarioRepository.findByUsername("developer").isPresent()) {
                return;
            }

            Empleado empleado = new Empleado();
            empleado.setNombres("Administrador");
            empleado.setApellidos("Principal");
            empleado.setDui("00000000-0");
            empleado.setTelefono("70000000");
            empleado.setEmail("developerdefault@gmail.local");
            empleado.setEstado(true);
            empleado.setDireccion("Dirección del administrador");
            Empleado empleadoGuardado = empleadoRepository.save(empleado);

            Usuario admin = new Usuario();
            admin.setUsername("developer");
            admin.setPassword(passwordEncoder.encode("default"));
            admin.setRol(Rol.ADMIN);
            admin.setEmpleado(empleadoGuardado);
            admin.setActivo(true);
            admin.setFechaCreacion(new Date());
            admin.setUltimoInicioSesion(null);
            admin.setCreadoPor(null); 

            usuarioRepository.save(admin);
        };
    }
}
