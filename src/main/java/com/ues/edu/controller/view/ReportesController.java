package com.ues.edu.controller.view;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ues.edu.serviceimp.ReportesAlquileresPDFServiceImpl;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ReportesController {

    @Autowired
    private ReportesAlquileresPDFServiceImpl reportesAlquileresPDFService;

    /** 
     * Página del menú de reportes
     */
    @GetMapping("/reportes")
    public String paginaReportes(Model model) {
        model.addAttribute("titulo", "Reportes del Sistema");
        return "reportes/reportes";
    }

    /**
     * Reporte PDF de ALQUILERES
     */
    @GetMapping("/reportes/alquileres/pdf")
    public void generarReporteAlquileresPDF(HttpServletResponse response) throws IOException {
        reportesAlquileresPDFService.generarPDF(response);
    }

}
