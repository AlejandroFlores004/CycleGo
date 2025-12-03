package com.ues.edu.controller.view;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ues.edu.model.Alquiler;
import com.ues.edu.model.Bicicleta;
import com.ues.edu.model.EstadoAlquiler;
import com.ues.edu.model.EstadoBicicleta;
import com.ues.edu.model.MetodoPago;
import com.ues.edu.model.PagoAlquiler;
import com.ues.edu.model.Usuario;
import com.ues.edu.service.IAlquilerService;
import com.ues.edu.service.IBicicletaService;
import com.ues.edu.service.IClienteService;
import com.ues.edu.service.IPagoAlquilerService;
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
    private final IPagoAlquilerService pagoAlquilerService;

    @GetMapping
    public String listarAlquileres(Model model) {
        model.addAttribute("titulo", "Alquileres");

        // Trae solo activos
        List<Alquiler> activos = alquilerService.listarActivos();
        model.addAttribute("alquileres", activos);

        // Trae finalizados para la sección inferior
        model.addAttribute("finalizados", alquilerService.listarFinalizados());

        // Construir mapa canFinalize: alquilerId -> true/false
        Map<Long, Boolean> canFinalize = new HashMap<>();
        LocalDate hoy = LocalDate.now(ZoneId.systemDefault());

        for (Alquiler a : activos) {
            boolean permitir = false;
            if (a.getFechaDevolucionEstimada() != null) {
                LocalDate fechaEstimada = a.getFechaDevolucionEstimada()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                // permitir si fechaEstimada es <= hoy
                permitir = !fechaEstimada.isAfter(hoy);
            }
            canFinalize.put(a.getIdAlquiler(), permitir);
        }

        model.addAttribute("canFinalize", canFinalize);

        return "alquiler/alquiler-lista";
    }


    @GetMapping("/nuevo")
    public String nuevoAlquiler(Model model) {
        Alquiler alquiler = new Alquiler();

        alquiler.setFechaAlquiler(new Date());
        alquiler.setEstado(EstadoAlquiler.ACTIVO);

        // Clientes: todos
        model.addAttribute("clientes", clienteService.listarTodos());
        // Bicicletas: SOLO DISPONIBLES
        model.addAttribute("bicicletas", bicicletaService.listarDisponibles());

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
            RedirectAttributes flash,
            Authentication authentication
    ) {

        boolean esNuevo = (alquiler.getIdAlquiler() == null);

        if (result.hasErrors()) {
            model.addAttribute("clientes", clienteService.listarTodos());
            if (esNuevo) {
                model.addAttribute("bicicletas", bicicletaService.listarDisponibles());
            } else {
                model.addAttribute("bicicletas", bicicletaService.listarTodas());
            }
            model.addAttribute("titulo", esNuevo ? "Nuevo alquiler" : "Editar alquiler");
            model.addAttribute("urlForm", "/alquileres/guardar");
            return "alquiler/alquiler-form";
        }

        // Usuario de registro
        if (alquiler.getUsuarioRegistro() == null && authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Usuario usuario = usuarioService.buscarPorUsername(username);
            alquiler.setUsuarioRegistro(usuario);
        }

        // Si es nuevo, asignar fecha y estado
        if (esNuevo) {
            alquiler.setFechaAlquiler(new Date());
            alquiler.setEstado(EstadoAlquiler.ACTIVO);
        }

        // 1) Guardamos el alquiler
        alquilerService.guardar(alquiler);

        // 2) Si es nuevo → marcar bicicleta como ALQUILADA
        if (esNuevo && alquiler.getBicicleta() != null) {
            Bicicleta bici = alquiler.getBicicleta();
            bici.setEstado(EstadoBicicleta.ALQUILADA);  // o "ALQUILADA" si es String
            bicicletaService.guardar(bici);
        }

        // 3) Redirecciones
        if (esNuevo) {
            flash.addFlashAttribute("success", "Alquiler guardado correctamente, ahora registre el pago.");
            return "redirect:/alquileres/" + alquiler.getIdAlquiler() + "/pago/nuevo";
        }

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

    // =========================
    //      PAGO ALQUILER
    // =========================

    @GetMapping("/{id}/pago/nuevo")
    public String nuevoPagoAlquiler(@PathVariable("id") Long idAlquiler,
                                    Model model,
                                    RedirectAttributes flash,
                                    Authentication authentication) {

        Alquiler alquiler = alquilerService.buscarPorId(idAlquiler);
        if (alquiler == null) {
            flash.addFlashAttribute("error", "El alquiler no existe");
            return "redirect:/alquileres";
        }

        // Si ya tiene pago, redirigimos a editar
        PagoAlquiler existente = pagoAlquilerService.buscarPorAlquiler(alquiler);
        if (existente != null) {
            return "redirect:/alquileres/" + idAlquiler + "/pago/editar";
        }

        PagoAlquiler pago = new PagoAlquiler();
        pago.setAlquiler(alquiler);
        // usuarioRegistro se asignará en el POST
        // fechaPago y estado se asignan en @PrePersist si están null

        model.addAttribute("alquiler", alquiler);
        model.addAttribute("pago", pago);
        model.addAttribute("metodosPago", MetodoPago.values());
        model.addAttribute("titulo", "Registrar pago de alquiler");
        model.addAttribute("urlForm", "/alquileres/" + idAlquiler + "/pago/guardar");

        return "alquiler/pago-alquiler-form";
    }

    @GetMapping("/{id}/pago/editar")
    public String editarPagoAlquiler(@PathVariable("id") Long idAlquiler,
                                     Model model,
                                     RedirectAttributes flash) {

        Alquiler alquiler = alquilerService.buscarPorId(idAlquiler);
        if (alquiler == null) {
            flash.addFlashAttribute("error", "El alquiler no existe");
            return "redirect:/alquileres";
        }

        PagoAlquiler pago = pagoAlquilerService.buscarPorAlquiler(alquiler);
        if (pago == null) {
            flash.addFlashAttribute("error", "Este alquiler aún no tiene pago registrado");
            return "redirect:/alquileres/" + idAlquiler + "/pago/nuevo";
        }

        model.addAttribute("alquiler", alquiler);
        model.addAttribute("pago", pago);
        model.addAttribute("metodosPago", MetodoPago.values());
        model.addAttribute("titulo", "Editar pago de alquiler");
        model.addAttribute("urlForm", "/alquileres/" + idAlquiler + "/pago/guardar");

        return "alquiler/pago-alquiler-form";
    }

    @PostMapping("/{id}/pago/guardar")
    public String guardarPagoAlquiler(@PathVariable("id") Long idAlquiler,
                                      @Valid PagoAlquiler pago,
                                      BindingResult result,
                                      Model model,
                                      RedirectAttributes flash,
                                      Authentication authentication) {

        Alquiler alquiler = alquilerService.buscarPorId(idAlquiler);
        if (alquiler == null) {
            flash.addFlashAttribute("error", "El alquiler no existe");
            return "redirect:/alquileres";
        }

        boolean esNuevo = (pago.getIdPagoAlquiler() == null);

        // Reforzamos el alquiler por seguridad
        pago.setAlquiler(alquiler);

        if (result.hasErrors()) {
            model.addAttribute("alquiler", alquiler);
            model.addAttribute("pago", pago);
            model.addAttribute("metodosPago", MetodoPago.values());
            model.addAttribute("titulo", esNuevo ? "Registrar pago de alquiler" : "Editar pago de alquiler");
            model.addAttribute("urlForm", "/alquileres/" + idAlquiler + "/pago/guardar");
            return "alquiler/pago-alquiler-form";
        }

        // Asignar usuarioRegistro
        if (pago.getUsuarioRegistro() == null && authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Usuario usuario = usuarioService.buscarPorUsername(username);
            pago.setUsuarioRegistro(usuario);
        }

        // fechaPago y estado los maneja @PrePersist si están null
        pagoAlquilerService.guardar(pago);

        flash.addFlashAttribute("success", "Pago de alquiler guardado correctamente");
        return "redirect:/alquileres";
    }

    private void cargarListas(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("bicicletas", bicicletaService.listar());
    }

    @GetMapping("/finalizar/{id}")
    public String finalizar(@PathVariable Long id, RedirectAttributes flash) {
        Alquiler alquiler = alquilerService.buscarPorId(id);
        if (alquiler == null) {
            flash.addFlashAttribute("error", "El alquiler no existe");
            return "redirect:/alquileres";
        }

        alquilerService.finalizarAlquiler(id);
        flash.addFlashAttribute("success", "Alquiler finalizado correctamente");
        return "redirect:/alquileres";
    }
}
