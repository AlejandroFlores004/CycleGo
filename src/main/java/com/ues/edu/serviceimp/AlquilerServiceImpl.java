package com.ues.edu.serviceimp;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ues.edu.model.Alquiler;
import com.ues.edu.model.Bicicleta;
import com.ues.edu.model.EstadoAlquiler;
import com.ues.edu.model.EstadoBicicleta;
import com.ues.edu.model.EstadoPago;
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

     @Override
    public List<Alquiler> listarFinalizados() {
        return alquilerRepository.findByEstado(EstadoAlquiler.FINALIZADO);
    }

    @Override
    @Transactional
    public void finalizarAlquiler(Long id) {
        Alquiler alquiler = alquilerRepository.findById(id).orElse(null);
        if (alquiler == null) return;

        // 1) marcar alquiler finalizado y fecha devolución real (ahora)
        alquiler.setEstado(EstadoAlquiler.FINALIZADO);
        Date ahora = new Date();
        alquiler.setFechaDevolucionReal(ahora);

        // 2) liberar bicicleta
        if (alquiler.getBicicleta() != null) {
            alquiler.getBicicleta().setEstado(EstadoBicicleta.DISPONIBLE);
            bicicletaRepository.save(alquiler.getBicicleta());
        }

        // 3) marcar pago como PAGADO y poner fechaPago = hoy si existe
        PagoAlquiler pago = pagoAlquilerRepository
                        .findByAlquiler(alquiler)
                        .orElse(null);
        if (pago != null) {
            // Si pago.estado es enum, usar el enum; si es String, setear "PAGADO"
            // ejemplo para String:
            pago.setEstado(EstadoPago.PAGADO);
            // setear fechaPago con 'ahora' (o con fecha sin hora si prefieres)
            pago.setFechaPago(ahora);
            pagoAlquilerRepository.save(pago);
        }

        // 4) guardar alquiler
        alquilerRepository.save(alquiler);
    }
}
