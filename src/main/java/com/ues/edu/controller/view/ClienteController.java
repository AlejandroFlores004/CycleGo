package com.ues.edu.controller.view;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ues.edu.model.Cliente;
import com.ues.edu.service.IClienteService;
import com.ues.edu.service.IUsuarioService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final IClienteService clienteService;
    private final IUsuarioService usuarioService;

    public ClienteController(IClienteService clienteService,
                             IUsuarioService usuarioService) {
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
    }


    @GetMapping
    public String listar(Model model) {
        List<Cliente> clientes = clienteService.listarTodos();

        model.addAttribute("clientes", clientes);
        model.addAttribute("titulo", "Listado de Clientes");
        model.addAttribute("activePage", "clientes");
        return "clientes/lista";
    }
    
    

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        Cliente cliente = new Cliente();

        model.addAttribute("cliente", cliente);
        model.addAttribute("titulo", "Nuevo Cliente");
        model.addAttribute("urlForm", "/clientes/guardar");
        model.addAttribute("modoEdicion", false);
        model.addAttribute("activePage", "clientes");
        model.addAttribute("usuarios", usuarioService.listarTodos());


        return "clientes/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model,
                         RedirectAttributes flash) {

        Cliente cliente = clienteService.buscarPorId(id);
        if (cliente == null) {
            flash.addFlashAttribute("error", "El Cliente no existe");
            return "redirect:/clientes";
        }

        model.addAttribute("cliente", cliente);
        model.addAttribute("titulo", "Editar Cliente");
        model.addAttribute("urlForm", "/clientes/guardar");
        model.addAttribute("modoEdicion", true);
        model.addAttribute("activePage", "clientes");
        model.addAttribute("usuarios", usuarioService.listarTodos());


        return "clientes/form";
    }

    @PostMapping("/guardar")
    public String guardar(
        @Valid @ModelAttribute("cliente") Cliente cliente,
        BindingResult result,
        @RequestParam(value = "modoEdicion", required= false, defaultValue = "false") boolean modoEdicion,
                        RedirectAttributes flash,
                        Model model) {

        if(result.hasErrors()) {
            model.addAttribute("titulo", modoEdicion ? "Editar Cliente" : "Nuevo Cliente");
            model.addAttribute("urlForm", "/clientes/guardar");
            model.addAttribute("modoEdicion", modoEdicion);
            model.addAttribute("activePage", "clientes");
            model.addAttribute("usuarios", usuarioService.listarTodos());

            return "clientes/form";
        }

        clienteService.guardar(cliente);
        flash.addFlashAttribute("success", "Cliente guardado correctamente");
        return "redirect:/clientes";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long id, 
    RedirectAttributes flash) {
        
        Cliente cliente = clienteService.buscarPorId(id);
        if (cliente == null) {
            flash.addFlashAttribute("error", "El Cliente no existe");
            return "redirect:/clientes";
        }

        clienteService.eliminar(id);
        flash.addFlashAttribute("success", "Cliente eliminado correctamente");
        return "redirect:/clientes";
    }
    
}
