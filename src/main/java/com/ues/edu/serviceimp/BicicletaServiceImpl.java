package com.ues.edu.serviceimp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ues.edu.model.Bicicleta;
import com.ues.edu.repository.BicicletaRepository;
import com.ues.edu.service.IBicicletaService;

@Service
public class BicicletaServiceImpl implements IBicicletaService {

    @Autowired
    private BicicletaRepository bicicletaRepository;

    @Override
    public List<Bicicleta> listar() {
        // todas sin paginar, ordenadas por fecha desc
        return bicicletaRepository.findAll(Sort.by("fechaCreacion").descending());
    }

    @Override
    public Bicicleta buscarPorId(Integer id) {
        return bicicletaRepository.findById(id).orElse(null);
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
