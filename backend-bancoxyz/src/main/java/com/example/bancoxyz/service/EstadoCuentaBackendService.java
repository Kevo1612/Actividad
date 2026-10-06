package com.example.bancoxyz.service;

import com.example.bancoxyz.model.EstadoCuenta;
import com.example.bancoxyz.repository.EstadoCuentaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoCuentaBackendService {

    private final EstadoCuentaRepository estadoCuentaRepository;

    public EstadoCuentaBackendService(
            EstadoCuentaRepository estadoCuentaRepository) {
        this.estadoCuentaRepository = estadoCuentaRepository;
    }

    public List<EstadoCuenta> obtenerEstadosPorCuenta(Long cuentaId) {
        return estadoCuentaRepository.findByCuentaId(cuentaId);
    }
}