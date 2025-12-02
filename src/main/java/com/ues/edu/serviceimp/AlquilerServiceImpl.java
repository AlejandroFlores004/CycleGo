package com.ues.edu.serviceimp;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ues.edu.model.Alquiler;
import com.ues.edu.model.Bicicleta;
import com.ues.edu.model.EstadoAlquiler;
import com.ues.edu.model.EstadoBicicleta;
import com.ues.edu.model.PagoAlquiler;
import com.ues.edu.repository.AlquilerRepository;
import com.ues.edu.repository.BicicletaRepository;
import com.ues.edu.repository.PagoAlquilerRepository;
import com.ues.edu.service.IAlquilerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AlquilerServiceImpl implements IAlquilerService {

    private final AlquilerRepository alquilerRepository;
    private final PagoAlquilerRepository pagoAlquilerRepository;
    private final BicicletaRepository bicicletaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Alquiler> listarTodos() {
        return alquilerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Alquiler buscarPorId(Long idAlquiler) {
        return alquilerRepository.findById(idAlquiler).orElse(null);
    }

    @Override
    public Alquiler guardar(Alquiler alquiler) {
        return alquilerRepository.save(alquiler);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Alquiler alquiler = alquilerRepository.findById(id).orElse(null);
        if (alquiler == null) {
            return;
        }

        PagoAlquiler pago = pagoAlquilerRepository
                        .findByAlquiler(alquiler)
                        .orElse(null);
        if (pago != null) {
            pagoAlquilerRepository.delete(pago);
        }

        if (alquiler.getBicicleta() != null) {
            Bicicleta bici = alquiler.getBicicleta();
            bici.setEstado(EstadoBicicleta.DISPONIBLE);
            bicicletaRepository.save(bici);
        }

        alquilerRepository.delete(alquiler);
    }

    @Override
    public List<Alquiler> listarActivos() {
        return alquilerRepository.findByEstado(EstadoAlquiler.ACTIVO);
    }
}
