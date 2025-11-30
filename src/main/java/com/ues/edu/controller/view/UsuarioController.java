package com.ues.edu.controller.view;


import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ues.edu.model.Usuario;
import com.ues.edu.model.Rol;
import com.ues.edu.model.Empleado;
import com.ues.edu.service.IUsuarioService;
import com.ues.edu.repository.EmpleadoRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final IUsuarioService usuarioService;
    private final EmpleadoRepository empleadoRepository;

    public UsuarioController(IUsuarioService usuarioService,
                             EmpleadoRepository empleadoRepository) {
        this.usuarioService = usuarioService;
        this.empleadoRepository = empleadoRepository;
    }

    // LISTAR
    @GetMapping
    public String listar(Model model) {
        List<Usuario> usuarios = usuarioService.listarTodos();

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("titulo", "Listado de usuarios");
        model.addAttribute("activePage", "usuarios");
        return "usuarios/lista";
    }

    // NUEVO
    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        Usuario usuario = new Usuario();
        usuario.setActivo(true);
        usuario.setFechaCreacion(new Date());

        model.addAttribute("usuario", usuario);
        model.addAttribute("titulo", "Nuevo usuario");
        model.addAttribute("urlForm", "/usuarios/guardar");
        model.addAttribute("modoEdicion", false);
        model.addAttribute("roles", Rol.values());
        model.addAttribute("empleadosDisponibles",
                empleadoRepository.findEmpleadosActivosSinUsuario());

        model.addAttribute("activePage", "usuarios");

        return "usuarios/form";
    }

    // EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model,
                         RedirectAttributes flash) {

        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario == null) {
            flash.addFlashAttribute("error", "El usuario no existe");
            return "redirect:/usuarios";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("titulo", "Editar usuario");
        model.addAttribute("urlForm", "/usuarios/guardar");
        model.addAttribute("modoEdicion", true);
        model.addAttribute("roles", Rol.values());
        // En edición NO permitimos cambiar empleado, así que no cargamos lista
        model.addAttribute("activePage", "usuarios");

        return "usuarios/form";
    }

    // GUARDAR (crear / editar)
    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            @RequestParam(name = "empleadoId", required = false) Long empleadoId,
            @RequestParam(name = "modoEdicion", required = false, defaultValue = "false") boolean modoEdicion,
            RedirectAttributes flash,
            Model model) {

        boolean esEdicion = (usuario.getIdUsuario() != null);

        // ===== VALIDAR CONTRASEÑA =====
        String pwd = usuario.getPassword();
        String pwdConfirm = usuario.getPasswordConfirm();

        // En creación: contraseña obligatoria
        if (!esEdicion) {
            if (pwd == null || pwd.isBlank()) {
                result.rejectValue("password", "NotBlank", "La contraseña es obligatoria");
            }
        }

        // Si se escribió contraseña (en nuevo o edición), confirmar
        if (pwd != null && !pwd.isBlank()) {
            if (pwdConfirm == null || !pwd.equals(pwdConfirm)) {
                result.rejectValue("passwordConfirm", "NoMatch", "Las contraseñas no coinciden");
            }
        }

        // ===== VALIDACIÓN / CARGA DE EMPLEADO =====
        if (!esEdicion) {
            // CREACIÓN: empleado viene por empleadoId
            if (empleadoId == null) {
                result.rejectValue("empleado", "NotNull", "Debe seleccionar un empleado");
            } else {
                Empleado empleado = empleadoRepository.findById(empleadoId).orElse(null);
                if (empleado == null) {
                    result.rejectValue("empleado", "NotNull", "Empleado no válido");
                } else {
                    usuario.setEmpleado(empleado);
                }
            }
        } else {
            // EDICIÓN: se recupera el existente para no perder info sensible
            Usuario existente = usuarioService.buscarPorId(usuario.getIdUsuario());
            if (existente == null) {
                flash.addFlashAttribute("error", "El usuario no existe");
                return "redirect:/usuarios";
            }

            // No se cambia empleado desde el form
            usuario.setEmpleado(existente.getEmpleado());
            usuario.setFechaCreacion(existente.getFechaCreacion());
            usuario.setCreadoPor(existente.getCreadoPor());

            // Si password viene vacía, conservamos la anterior
            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                usuario.setPassword(existente.getPassword());
            }
        }

        // ===== SI HAY ERRORES, REGRESAMOS AL FORM =====
        if (result.hasErrors()) {
            model.addAttribute("titulo", esEdicion ? "Editar usuario" : "Nuevo usuario");
            model.addAttribute("urlForm", "/usuarios/guardar");
            model.addAttribute("modoEdicion", esEdicion);
            model.addAttribute("roles", Rol.values());
            model.addAttribute("activePage", "usuarios");

            if (!esEdicion) {
                model.addAttribute("empleadosDisponibles",
                        empleadoRepository.findEmpleadosActivosSinUsuario());
            }

            return "usuarios/form";
        }

        // Fecha de creación en alta
        if (!esEdicion && usuario.getFechaCreacion() == null) {
            usuario.setFechaCreacion(new Date());
        }

        usuarioService.guardar(usuario);

        flash.addFlashAttribute("success", "Usuario guardado correctamente");
        return "redirect:/usuarios";
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long id,
                           RedirectAttributes flash) {

        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario == null) {
            flash.addFlashAttribute("error", "El usuario no existe");
        } else {
            usuarioService.eliminar(id);
            flash.addFlashAttribute("success", "Usuario eliminado correctamente");
        }

        return "redirect:/usuarios";
    }
}