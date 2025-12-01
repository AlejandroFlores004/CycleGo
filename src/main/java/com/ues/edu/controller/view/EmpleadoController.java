package com.ues.edu.controller.view;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ues.edu.model.Empleado;
import com.ues.edu.service.IEmpleadoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    private final IEmpleadoService empleadoService;

    public EmpleadoController(IEmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping
    public String listar(Model model) {
        List<Empleado> empleados = empleadoService.listarTodos();

        model.addAttribute("empleados", empleados);
        model.addAttribute("titulo", "Listado de Empleados");
        model.addAttribute("activePage", "empleados"); 
        return "empleados/lista";
    }

   
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        Empleado empleado = new Empleado();

        model.addAttribute("empleado", empleado);
        model.addAttribute("titulo", "Nuevo Empleado");
        model.addAttribute("urlForm", "/empleados/guardar");
        model.addAttribute("modoEdicion", false);
        model.addAttribute("activePage", "empleados");

        return "empleados/form"; 
    }

   
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model,
                         RedirectAttributes flash) {

        Empleado empleado = empleadoService.buscarPorId(id);
        if (empleado == null) {
            flash.addFlashAttribute("error", "El Empleado no existe");
            return "redirect:/empleados";
        }

        model.addAttribute("empleado", empleado);
        model.addAttribute("titulo", "Editar Empleado");
        model.addAttribute("urlForm", "/empleados/guardar");
        model.addAttribute("modoEdicion", true);
        model.addAttribute("activePage", "empleados");

        return "empleados/form";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("empleado") Empleado empleado,
            BindingResult result,
            @RequestParam(name = "modoEdicion", required = false, defaultValue = "false") boolean modoEdicion,
            RedirectAttributes flash,
            Model model) {


        if (result.hasErrors()) {
            model.addAttribute("titulo", modoEdicion ? "Editar Empleado" : "Nuevo Empleado");
            model.addAttribute("urlForm", "/empleados/guardar");
            model.addAttribute("modoEdicion", modoEdicion);
            model.addAttribute("activePage", "empleados");
            return "empleados/form";
        }

        empleadoService.guardar(empleado);

        flash.addFlashAttribute("success", "Empleado guardado correctamente");
        return "redirect:/empleados";
    }


    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long id,
                           RedirectAttributes flash) {

        Empleado empleado = empleadoService.buscarPorId(id);
        if (empleado == null) {
            flash.addFlashAttribute("error", "El Empleado no existe");
        } else {
            empleadoService.eliminar(id);
            flash.addFlashAttribute("success", "Empleado eliminado correctamente");
        }

        return "redirect:/empleados";
    }
}