package com.ues.edu.controller.view;

import com.ues.edu.model.Bicicleta;
import com.ues.edu.model.Empleado;
import com.ues.edu.model.EstadoBicicleta;
import com.ues.edu.model.EstadoMantenimiento;
import com.ues.edu.model.Mantenimiento;
import com.ues.edu.model.Rol;
import com.ues.edu.model.Usuario;
import com.ues.edu.repository.BicicletaRepository;
import com.ues.edu.repository.EmpleadoRepository;
import com.ues.edu.repository.UsuarioRepository;
import com.ues.edu.service.IBicicletaService;
import com.ues.edu.service.IMantenimientoService;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mantenimientos")
public class MantenimientoController {

    private final IMantenimientoService mantenimientoService;
    private final BicicletaRepository bicicletaRepository;
    private final UsuarioRepository usuarioRepository;
    private final IBicicletaService bicicletaService;

    public MantenimientoController(IMantenimientoService mantenimientoService,
                                   BicicletaRepository bicicletaRepository,
                                   UsuarioRepository usuarioRepository,
                                   IBicicletaService bicicletaService) {
        this.mantenimientoService = mantenimientoService;
        this.bicicletaRepository = bicicletaRepository;
        this.bicicletaService = bicicletaService;
        this.usuarioRepository = usuarioRepository;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    private void cargarCombos(Model model) {
        List<Bicicleta> bicicletas = bicicletaRepository.findAll();
        List<Usuario> usuariosMantenimiento = usuarioRepository.findByRolAndActivoTrue(Rol.MANTENIMIENTO);
        List<Empleado> empleadosParaMantenimiento = usuariosMantenimiento.stream()
            .map(Usuario::getEmpleado)          // extrae empleado desde cada usuario
            .filter(Objects::nonNull)           // evita nulls
            .collect(Collectors.toList());
        model.addAttribute("bicicletas", bicicletas);
        model.addAttribute("empleados", empleadosParaMantenimiento);
    }

    @GetMapping
    public String listar(Model model) {
        List<Mantenimiento> lista = mantenimientoService.findAll();
        model.addAttribute("mantenimientos", lista);

        // Agrupar por estado
        List<Mantenimiento> pendientes = lista.stream()
                .filter(m -> m.getEstado() == EstadoMantenimiento.PENDIENTE)
                .collect(Collectors.toList());

        List<Mantenimiento> enProceso = lista.stream()
                .filter(m -> m.getEstado() == EstadoMantenimiento.EN_PROCESO)
                .collect(Collectors.toList());

        List<Mantenimiento> finalizados = lista.stream()
                .filter(m -> m.getEstado() == EstadoMantenimiento.COMPLETADO)
                .collect(Collectors.toList());

        model.addAttribute("pendientes", pendientes);
        model.addAttribute("enProceso", enProceso);
        model.addAttribute("finalizados", finalizados);

        return "mantenimientos/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        Mantenimiento mantenimiento = new Mantenimiento();
        // Estado por defecto para nuevos registros
        mantenimiento.setEstado(EstadoMantenimiento.PENDIENTE);
        model.addAttribute("bicicletas", bicicletaService.listarDisponibles());

        model.addAttribute("mantenimiento", mantenimiento);
        cargarCombos(model);
        return "mantenimientos/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("mantenimiento") Mantenimiento mantenimiento,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            cargarCombos(model);
            return "mantenimientos/form";
        }

        mantenimientoService.save(mantenimiento);
        redirectAttributes.addFlashAttribute("success", "Mantenimiento guardado correctamente");
        return "redirect:/mantenimientos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Mantenimiento> opt = mantenimientoService.findById(id);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Mantenimiento no encontrado");
            return "redirect:/mantenimientos";
        }
        model.addAttribute("mantenimiento", opt.get());
        cargarCombos(model);
        return "mantenimientos/form";
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Mantenimiento> opt = mantenimientoService.findById(id);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Mantenimiento no encontrado");
            return "redirect:/mantenimientos";
        }
        model.addAttribute("mantenimiento", opt.get());
        return "mantenimientos/view";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        mantenimientoService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Mantenimiento eliminado");
        return "redirect:/mantenimientos";
    }

    @PostMapping("/{id}/iniciar")
    public String marcarEnProceso(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Mantenimiento> opt = mantenimientoService.findById(id);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Mantenimiento no encontrado");
            return "redirect:/mantenimientos";
        }

        Mantenimiento m = opt.get();

        if (m.getEstado() != EstadoMantenimiento.PENDIENTE) {
            redirectAttributes.addFlashAttribute("error", "Solo se pueden iniciar mantenimientos en estado PENDIENTE");
            return "redirect:/mantenimientos";
        }

        // Cambiar estado del mantenimiento
        m.setEstado(EstadoMantenimiento.EN_PROCESO);

        // Cambiar estado de la bicicleta asociada
        Bicicleta bici = m.getBicicleta();
        if (bici != null) {
            // 🔹 Versión con ENUM
            // bici.setEstado(EstadoBicicleta.MANTENIMIENTO);

            // 🔹 Versión con String
            bici.setEstado(EstadoBicicleta.MANTENIMIENTO);

            bicicletaRepository.save(bici); // actualizar bici en BD
        }

        mantenimientoService.save(m); // guardar mantenimiento actualizado

        redirectAttributes.addFlashAttribute("success", "Mantenimiento " + m.getIdMantenimiento() + " marcado como EN PROCESO");
        return "redirect:/mantenimientos";
    }

    @PostMapping("/{id}/finalizar")
    public String marcarFinalizado(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Mantenimiento> opt = mantenimientoService.findById(id);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Mantenimiento no encontrado");
            return "redirect:/mantenimientos";
        }

        Mantenimiento m = opt.get();

        if (m.getEstado() != EstadoMantenimiento.EN_PROCESO) {
            redirectAttributes.addFlashAttribute("error", "Solo se pueden finalizar mantenimientos EN PROCESO");
            return "redirect:/mantenimientos";
        }

        // Cambiar estado del mantenimiento
        m.setEstado(EstadoMantenimiento.COMPLETADO);

        // Cambiar estado de la bicicleta asociada
        Bicicleta bici = m.getBicicleta();
        if (bici != null) {
            // 🔹 Versión con ENUM
            // bici.setEstado(EstadoBicicleta.DISPONIBLE);

            // 🔹 Versión con String
            bici.setEstado(EstadoBicicleta.DISPONIBLE);

            bicicletaRepository.save(bici);
        }

        mantenimientoService.save(m);

        redirectAttributes.addFlashAttribute("success", "Mantenimiento " + m.getIdMantenimiento() + " marcado como FINALIZADO");
        return "redirect:/mantenimientos";
    }

}
