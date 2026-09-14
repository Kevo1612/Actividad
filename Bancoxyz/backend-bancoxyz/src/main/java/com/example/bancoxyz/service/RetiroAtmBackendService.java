package com.example.bancoxyz.service;

import com.example.bancoxyz.dto.RetiroAtmBackendRequestDTO;
import com.example.bancoxyz.dto.RetiroAtmBackendResponseDTO;
import com.example.bancoxyz.model.AtmOperacion;
import com.example.bancoxyz.model.CuentaInteres;
import com.example.bancoxyz.repository.AtmOperacionRepository;
import com.example.bancoxyz.repository.CuentaInteresRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RetiroAtmBackendService {

    private final CuentaInteresRepository cuentaInteresRepository;
    private final AtmOperacionRepository atmOperacionRepository;

    public RetiroAtmBackendService(
            CuentaInteresRepository cuentaInteresRepository,
            AtmOperacionRepository atmOperacionRepository) {

        this.cuentaInteresRepository = cuentaInteresRepository;
        this.atmOperacionRepository = atmOperacionRepository;
    }

    public RetiroAtmBackendResponseDTO realizarRetiro(
            Long cuentaId,
            RetiroAtmBackendRequestDTO request) {

        CuentaInteres cuenta = cuentaInteresRepository.findById(cuentaId)
                .orElseThrow(() ->
                        new RuntimeException("Cuenta no encontrada"));

        BigDecimal saldoBase = cuenta.getSaldoFinal();

        List<AtmOperacion> operaciones =
                atmOperacionRepository.findByCuentaIdOrderByFechaDesc(cuentaId);

        BigDecimal totalRetiros = operaciones.stream()
                .map(AtmOperacion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoDisponible =
                saldoBase.subtract(totalRetiros);

        BigDecimal montoRetiro = request.getMonto();

        if (montoRetiro.compareTo(saldoDisponible) > 0) {
            throw new IllegalArgumentException(
                    "Saldo insuficiente para realizar el retiro");
        }

        AtmOperacion operacion = new AtmOperacion();

        operacion.setCuentaId(cuentaId);
        operacion.setTipoOperacion("RETIRO");
        operacion.setMonto(montoRetiro);
        operacion.setFecha(LocalDateTime.now());

        atmOperacionRepository.save(operacion);

        BigDecimal nuevoSaldo =
                saldoDisponible.subtract(montoRetiro);

        RetiroAtmBackendResponseDTO respuesta =
                new RetiroAtmBackendResponseDTO();

        respuesta.setCuentaId(cuentaId);
        respuesta.setMontoRetirado(montoRetiro);
        respuesta.setSaldoDisponible(nuevoSaldo);
        respuesta.setTipoOperacion("RETIRO");
        respuesta.setFecha(operacion.getFecha());
        respuesta.setMensaje("Retiro realizado correctamente");

        return respuesta;
    }
}