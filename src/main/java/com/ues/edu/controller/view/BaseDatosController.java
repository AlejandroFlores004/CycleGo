package com.ues.edu.controller.view;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ues.edu.service.DatabaseBackupService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/basedatos")
public class BaseDatosController {

    @Autowired
    private DatabaseBackupService databaseBackupService;

    @GetMapping
    public String verPaginaBaseDatos(Model model) {
        model.addAttribute("activePage", "basedatos");
        return "basedatos/basedatos"; // ruta del template thymeleaf
    }

    @GetMapping("/backup")
    public void descargarBackup(HttpServletResponse response) throws IOException {

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        String fileName = "backup_bd_" + timestamp + ".backup";

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        databaseBackupService.generarBackup(response.getOutputStream());
        response.flushBuffer();
    }

    @PostMapping("/restaurar")
    public String restaurarBackup(@RequestParam("archivo") MultipartFile archivo,
                                  RedirectAttributes redirectAttributes) {
        if (archivo == null || archivo.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debes seleccionar un archivo de backup.");
            return "redirect:/basedatos";
        }

        try {
            databaseBackupService.restaurarBackup(archivo.getInputStream());
            redirectAttributes.addFlashAttribute("success", "Base de datos restaurada correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al restaurar la base de datos: " + e.getMessage());
        }

        return "redirect:/basedatos";
    }
}
