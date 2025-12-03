package com.ues.edu.controller.view;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    // LISTAR
    @GetMapping
    public String listar(@RequestParam(name = "page", defaultValue = "0") int page,
                         Model model) {

        Pageable pageable = PageRequest.of(page, 5, Sort.by("fechaCreacion").descending());
        Page<Bicicleta> pagina = bicicletaService.listar(pageable);

        model.addAttribute("titulo", "Gestión de Bicicletas");
        model.addAttribute("pagina", pagina);
        model.addAttribute("listaBicicletas", pagina.getContent());
        model.addAttribute("totalPages", pagina.getTotalPages());
        model.addAttribute("totalElements", pagina.getTotalElements());
        model.addAttribute("currentPage", page);

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
                auth != null ? auth.getName() : ""
        );

        return "bicicletas/form";
    }

    // EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model,
                         RedirectAttributes flash, Authentication auth) {

        Optional<Bicicleta> opt = bicicletaService.buscarPorId(id);

        if (opt.isEmpty()) {
            flash.addFlashAttribute("error", "La bicicleta no existe");
            return "redirect:/bicicletas";
        }

        Bicicleta bicicleta = opt.get();

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

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("bicicleta") Bicicleta bicicleta,
                          BindingResult result,
                          Model model,
                          RedirectAttributes flash,
                          Authentication authentication) {

        if (result.hasErrors()) {

            model.addAttribute("titulo",
                    bicicleta.getIdBicicleta() == null ? "Nueva Bicicleta" : "Editar Bicicleta");

            model.addAttribute("estados",
                    Arrays.asList("DISPONIBLE", "ALQUILADA", "MANTENIMIENTO"));

            model.addAttribute("usuarioRegistroNombre",
                    authentication != null ? authentication.getName() : "");

            return "bicicletas/form";
        }

        // SI ES NUEVA → NO TOCAR NADA
        if (bicicleta.getIdBicicleta() == null) {

            // SOLO asignar usuario si hay auth
            if (authentication != null) {
                usuarioRepository.findByUsername(authentication.getName())
                        .ifPresent(bicicleta::setUsuarioRegistro);
            }

            // fechaCreacion y estado lo hace @PrePersist
        }

        // SI ES EDICIÓN → conservar campos protegidos
        else {
            Bicicleta original = bicicletaService.buscarPorId(bicicleta.getIdBicicleta()).get();
            bicicleta.setFechaCreacion(original.getFechaCreacion());
            bicicleta.setUsuarioRegistro(original.getUsuarioRegistro());
        }

        bicicletaService.guardar(bicicleta);
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
