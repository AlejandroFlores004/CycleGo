package com.ues.edu.serviceimp;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ues.edu.model.Bicicleta;
import com.ues.edu.model.EstadoBicicleta;
import com.ues.edu.repository.BicicletaRepository;
import com.ues.edu.service.IBicicletaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BicicletaServiceImpl implements IBicicletaService {

    private final BicicletaRepository bicicletaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Bicicleta> listarTodas() {
        return bicicletaRepository.findAll();
    }

    @Override
    public List<Bicicleta> listarDisponibles() {
        return bicicletaRepository.findByEstado(EstadoBicicleta.DISPONIBLE);
        // o, si es String:
        // return bicicletaRepository.findByEstado("DISPONIBLE");
    }

    @Override
    @Transactional(readOnly = true)
    public Bicicleta buscarPorId(Long idBicicleta) {
        return bicicletaRepository.findById(idBicicleta).orElse(null);
    }

    @Override
    public Bicicleta guardar(Bicicleta bicicleta) {
        return bicicletaRepository.save(bicicleta);
    }

    @Override
    public void eliminar(Long idBicicleta) {
        bicicletaRepository.deleteById(idBicicleta);
    }
}
