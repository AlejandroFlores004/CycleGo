package com.ues.edu.serviceimp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ues.edu.dtos.IAlquilerReporteDTO;
import com.ues.edu.repository.AlquilerRepository;

import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class ReportesAlquileresPDFServiceImpl {

    @Autowired
    private AlquilerRepository alquilerRepository;

    private void generarReporte(InputStream stream, HttpServletResponse response,
                                List<IAlquilerReporteDTO> data) throws IOException {
        try {
            JasperReport jasperReport = JasperCompileManager.compileReport(stream);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("createdBy", "Sistema de Alquileres CycleGo");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=alquileres.pdf");

            OutputStream outStream = response.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
        } catch (JRException e) {
            throw new IOException("Error al generar el PDF de alquileres: ", e);
        }
    }

    public void generarPDF(HttpServletResponse response) throws IOException {
        List<IAlquilerReporteDTO> data = alquilerRepository.findDatosReporteAlquileres();
        InputStream jrxmlStream = getClass().getResourceAsStream("/reportes/AlquileresReporte.jrxml");
        if (jrxmlStream == null) {
            throw new IOException("No se encontró el archivo /reportes/AlquileresReporte.jrxml en el classpath");
        }
        generarReporte(jrxmlStream, response, data);
    }
}
