package com.ues.edu.serviceimp;   // usa el paquete correcto

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ues.edu.model.Bicicleta;
import com.ues.edu.repository.BicicletaRepository;
import com.ues.edu.service.IBicicletaService;

@Service
public class BicicletaServiceImpl implements IBicicletaService {

    @Autowired
    private BicicletaRepository bicicletaRepository;

    @Override
    public Page<Bicicleta> listar(Pageable pageable) {
        return bicicletaRepository.findAll(pageable);
    }

    @Override
    public Optional<Bicicleta> buscarPorId(Integer id) {
        return bicicletaRepository.findById(id);
    }

    @Override
    public void guardar(Bicicleta bicicleta) {
        bicicletaRepository.save(bicicleta);
    }

    @Override
    public void eliminar(Integer id) {
        bicicletaRepository.deleteById(id);
    }
    
}
