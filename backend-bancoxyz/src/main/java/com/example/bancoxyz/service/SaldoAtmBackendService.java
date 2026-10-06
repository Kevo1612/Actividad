package com.example.bancoxyz.service;

import com.example.bancoxyz.model.AtmOperacion;
import com.example.bancoxyz.model.CuentaInteres;
import com.example.bancoxyz.repository.AtmOperacionRepository;
import com.example.bancoxyz.repository.CuentaInteresRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SaldoAtmBackendService {

    private final CuentaInteresRepository cuentaInteresRepository;
    private final AtmOperacionRepository atmOperacionRepository;

    public SaldoAtmBackendService(
            CuentaInteresRepository cuentaInteresRepository,
            AtmOperacionRepository atmOperacionRepository) {

        this.cuentaInteresRepository = cuentaInteresRepository;
        this.atmOperacionRepository = atmOperacionRepository;
    }

    public BigDecimal obtenerSaldo(Long cuentaId) {

        CuentaInteres cuenta = cuentaInteresRepository.findById(cuentaId)
                .orElseThrow(() ->
                        new RuntimeException("Cuenta no encontrada"));

        BigDecimal saldoBase = cuenta.getSaldoFinal();

        List<AtmOperacion> operaciones =
                atmOperacionRepository.findByCuentaIdOrderByFechaDesc(cuentaId);

        BigDecimal totalRetiros = operaciones.stream()
                .map(AtmOperacion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return saldoBase.subtract(totalRetiros);
    }
}