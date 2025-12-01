package com.ues.edu.serviceimp;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ues.edu.model.Empleado;
import com.ues.edu.repository.EmpleadoRepository;
import com.ues.edu.service.IEmpleadoService;

@Service 
@Transactional 
public class EmpleadoServiceImpl implements IEmpleadoService {


    private final EmpleadoRepository empleadoRepository;

    
    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }


    @Override
    public Empleado guardar(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    @Override
    @Transactional(readOnly = true) 
    public List<Empleado> listarTodos() {
        return empleadoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Empleado buscarPorId(Long id) {
        Optional<Empleado> empleado = empleadoRepository.findById(id);
        return empleado.orElse(null);
    }

    @Override
    public void eliminar(Long id) {
        empleadoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Empleado> findEmpleadosActivosSinUsuario() {
        return empleadoRepository.findEmpleadosActivosSinUsuario();
    }
}