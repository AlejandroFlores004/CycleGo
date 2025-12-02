package com.ues.edu.serviceimp;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ues.edu.model.Alquiler;
import com.ues.edu.repository.AlquilerRepository;
import com.ues.edu.service.IAlquilerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AlquilerServiceImpl implements IAlquilerService {

    private final AlquilerRepository alquilerRepository;

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
    public void eliminar(Long idAlquiler) {
        alquilerRepository.deleteById(idAlquiler);
    }
}
