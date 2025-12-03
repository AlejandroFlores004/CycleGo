package com.ues.edu.controller.view;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ues.edu.model.Bicicleta;
import com.ues.edu.model.Usuario;
import com.ues.edu.repository.UsuarioRepository;
import com.ues.edu.service.IBicicletaService;

@Controller
@RequestMapping("/bicicletas")
public class BicicletaController {

    @Autowired
    private IBicicletaService bicicletaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // LISTA SIN PAGINADOR
    @GetMapping
    public String listar(Model model) {

        List<Bicicleta> lista = bicicletaService.listar();

        model.addAttribute("titulo", "Listado de bicicletas");
        model.addAttribute("listaBicicletas", lista);
        model.addAttribute("totalElements", lista.size());

        return "bicicletas/lista";
    }

    // NUEVA
    @GetMapping("/nueva")
    public String nueva(Model model, Authentication auth) {

        Bicicleta bicicleta = new Bicicleta();

        model.addAttribute("titulo", "Nueva Bicicleta");
        model.addAttribute("bicicleta", bicicleta);
        model.addAttribute("estados",
                Arrays.asList("DISPONIBLE", "ALQUILADA", "MANTENIMIENTO"));
        model.addAttribute("usuarioRegistroNombre",
                auth != null ? auth.getName() : "");

        return "bicicletas/form";
    }

    // EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id,
                         Model model,
                         RedirectAttributes flash,
                         Authentication auth) {

        Bicicleta bicicleta = bicicletaService.buscarPorId(id);

        if (bicicleta == null) {
            flash.addFlashAttribute("error", "La bicicleta no existe");
            return "redirect:/bicicletas";
        }

        String usuarioRegistroNombre =
                bicicleta.getUsuarioRegistro() != null
                        ? bicicleta.getUsuarioRegistro().getUsername()
                        : (auth != null ? auth.getName() : "");

        model.addAttribute("titulo", "Editar Bicicleta");
        model.addAttribute("bicicleta", bicicleta);
        model.addAttribute("estados",
                Arrays.asList("DISPONIBLE", "ALQUILADA", "MANTENIMIENTO"));
        model.addAttribute("usuarioRegistroNombre", usuarioRegistroNombre);

        return "bicicletas/form";
    }

    // GUARDAR (CREAR / EDITAR)
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("bicicleta") Bicicleta bicicleta,
                          BindingResult result,
                          Model model,
                          RedirectAttributes flash,
                          Authentication authentication) {

        // Validaciones de Bean Validation
        if (result.hasErrors()) {
            model.addAttribute("titulo",
                    bicicleta.getIdBicicleta() == null ? "Nueva Bicicleta" : "Editar Bicicleta");
            model.addAttribute("estados",
                    Arrays.asList("DISPONIBLE", "ALQUILADA", "MANTENIMIENTO"));
            model.addAttribute("usuarioRegistroNombre",
                    authentication != null ? authentication.getName() : "");
            return "bicicletas/form";
        }

        // NUEVA
        if (bicicleta.getIdBicicleta() == null) {

            if (authentication != null) {
                usuarioRepository.findByUsername(authentication.getName())
                        .ifPresent(bicicleta::setUsuarioRegistro);
            }
            // fechaCreacion y estado se setean en @PrePersist

        } else {
            // EDICIÓN: conservar fechaCreacion y usuarioRegistro
            Bicicleta original = bicicletaService.buscarPorId(bicicleta.getIdBicicleta());
            if (original == null) {
                flash.addFlashAttribute("error", "La bicicleta no existe");
                return "redirect:/bicicletas";
            }
            bicicleta.setFechaCreacion(original.getFechaCreacion());
            bicicleta.setUsuarioRegistro(original.getUsuarioRegistro());
        }

        try {
            bicicletaService.guardar(bicicleta);
        } catch (DataIntegrityViolationException e) {
            // Código duplicado u otra violación de integridad
            result.rejectValue("codigo", "codigo.duplicado",
                    "Ya existe una bicicleta con ese código");

            model.addAttribute("titulo",
                    bicicleta.getIdBicicleta() == null ? "Nueva Bicicleta" : "Editar Bicicleta");
            model.addAttribute("estados",
                    Arrays.asList("DISPONIBLE", "ALQUILADA", "MANTENIMIENTO"));
            model.addAttribute("usuarioRegistroNombre",
                    authentication != null ? authentication.getName() : "");

            return "bicicletas/form";
        }

        flash.addFlashAttribute("success", "Bicicleta guardada correctamente");
        return "redirect:/bicicletas";
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id,
                           RedirectAttributes flash) {

        bicicletaService.eliminar(id);
        flash.addFlashAttribute("success", "Bicicleta eliminada correctamente");

        return "redirect:/bicicletas";
    }
}
