package com.ues.edu.serviceimp;

import com.ues.edu.model.Mantenimiento;
import com.ues.edu.repository.MantenimientoRepository;
import com.ues.edu.service.IMantenimientoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MantenimientoServiceImpl implements IMantenimientoService {

    private final MantenimientoRepository mantenimientoRepository;

    public MantenimientoServiceImpl(MantenimientoRepository mantenimientoRepository) {
        this.mantenimientoRepository = mantenimientoRepository;
    }

    @Override
    public List<Mantenimiento> findAll() {
        return mantenimientoRepository.findAll();
    }

    @Override
    public Optional<Mantenimiento> findById(Long id) {
        return mantenimientoRepository.findById(id);
    }

    @Override
    public Mantenimiento save(Mantenimiento mantenimiento) {
        return mantenimientoRepository.save(mantenimiento);
    }

    @Override
    public void deleteById(Long id) {
        mantenimientoRepository.deleteById(id);
    }
}
