package com.example.bancoxyz.service;

import com.example.bancoxyz.model.CuentaInteres;
import com.example.bancoxyz.repository.CuentaInteresRepository;
import org.springframework.stereotype.Service;

@Service
public class CuentaBackendService {

    private final CuentaInteresRepository cuentaInteresRepository;

    public CuentaBackendService(CuentaInteresRepository cuentaInteresRepository) {
        this.cuentaInteresRepository = cuentaInteresRepository;
    }

    public CuentaInteres obtenerCuenta(Long id) {
        return cuentaInteresRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Cuenta no encontrada"));
    }
}