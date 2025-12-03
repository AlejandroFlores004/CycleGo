package com.ues.edu.controller.view;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ues.edu.model.Alquiler;
import com.ues.edu.model.EstadoAlquiler;
import com.ues.edu.service.IAlquilerService;
import com.ues.edu.service.IBicicletaService;
import com.ues.edu.service.IClienteService;
import com.ues.edu.service.IUsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/alquileres")
@RequiredArgsConstructor
public class AlquilerController {

    private final IAlquilerService alquilerService;
    private final IClienteService clienteService;
    private final IBicicletaService bicicletaService;
    private final IUsuarioService usuarioService;

    @GetMapping
    public String listarAlquileres(Model model) {
        model.addAttribute("titulo", "Gestión de alquileres");
        model.addAttribute("alquileres", alquilerService.listarTodos());
        return "alquiler/alquiler-lista";
    }

    @GetMapping("/nuevo")
    public String nuevoAlquiler(Model model) {
        Alquiler alquiler = new Alquiler();
        alquiler.setFechaAlquiler(new Date());       // fecha actual
        alquiler.setEstado(EstadoAlquiler.ACTIVO);   // ajusta si tu enum tiene otro nombre

        cargarListas(model);
        model.addAttribute("alquiler", alquiler);
        model.addAttribute("titulo", "Nuevo alquiler");
        model.addAttribute("urlForm", "/alquileres/guardar");
        return "alquiler/alquiler-form";
    }

    @GetMapping("/editar/{id}")
    public String editarAlquiler(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {

        Alquiler alquiler = alquilerService.buscarPorId(id);
        if (alquiler == null) {
            flash.addFlashAttribute("error", "El alquiler no existe");
            return "redirect:/alquileres";
        }

        cargarListas(model);
        model.addAttribute("alquiler", alquiler);
        model.addAttribute("titulo", "Editar alquiler");
        model.addAttribute("urlForm", "/alquileres/guardar");
        return "alquiler/alquiler-form";
    }

    @PostMapping("/guardar")
    public String guardarAlquiler(
            @Valid Alquiler alquiler,
            BindingResult result,
            Model model,
            RedirectAttributes flash) {

        if (result.hasErrors()) {
            cargarListas(model);
            model.addAttribute("titulo", alquiler.getIdAlquiler() == null ? "Nuevo alquiler" : "Editar alquiler");
            model.addAttribute("urlForm", "/alquileres/guardar");
            return "alquiler/alquiler-form";
        }

        // Si quieres asegurar fecha de alquiler cuando es nuevo
        if (alquiler.getIdAlquiler() == null && alquiler.getFechaAlquiler() == null) {
            alquiler.setFechaAlquiler(new Date());
        }

        alquilerService.guardar(alquiler);
        flash.addFlashAttribute("success", "Alquiler guardado correctamente");
        return "redirect:/alquileres";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarAlquiler(@PathVariable("id") Long id, RedirectAttributes flash) {

        Alquiler alquiler = alquilerService.buscarPorId(id);
        if (alquiler == null) {
            flash.addFlashAttribute("error", "El alquiler no existe");
        } else {
            alquilerService.eliminar(id);
            flash.addFlashAttribute("success", "Alquiler eliminado correctamente");
        }

        return "redirect:/alquileres";
    }

    private void cargarListas(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("bicicletas", bicicletaService.listar());
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("estados", EstadoAlquiler.values());
    }
}
