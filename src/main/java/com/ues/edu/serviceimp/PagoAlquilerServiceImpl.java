package com.ues.edu.serviceimp;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ues.edu.model.Alquiler;
import com.ues.edu.model.PagoAlquiler;
import com.ues.edu.repository.PagoAlquilerRepository;
import com.ues.edu.service.IPagoAlquilerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PagoAlquilerServiceImpl implements IPagoAlquilerService {

    private final PagoAlquilerRepository pagoAlquilerRepository;

    @Override
    public List<PagoAlquiler> listarTodos() {
        return pagoAlquilerRepository.findAll();
    }

    @Override
    public PagoAlquiler buscarPorId(Long id) {
        return pagoAlquilerRepository.findById(id).orElse(null);
    }

    @Override
    public PagoAlquiler buscarPorAlquiler(Alquiler alquiler) {
        return pagoAlquilerRepository.findByAlquiler(alquiler).orElse(null);
    }

    @Override
    public void guardar(PagoAlquiler pagoAlquiler) {
        pagoAlquilerRepository.save(pagoAlquiler);
    }

    @Override
    public void eliminar(Long id) {
        pagoAlquilerRepository.deleteById(id);
    }
}
