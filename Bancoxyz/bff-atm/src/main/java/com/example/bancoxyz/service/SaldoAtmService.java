package com.example.bancoxyz.service;

import com.example.bancoxyz.client.SaldoAtmBackendClient;
import com.example.bancoxyz.dto.SaldoAtmDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SaldoAtmService {

    private final SaldoAtmBackendClient saldoAtmBackendClient;

    public SaldoAtmService(
            SaldoAtmBackendClient saldoAtmBackendClient) {

        this.saldoAtmBackendClient = saldoAtmBackendClient;
    }

    public SaldoAtmDTO obtenerSaldo(Long cuentaId) {

        BigDecimal saldo =
                saldoAtmBackendClient.obtenerSaldo(cuentaId);

        SaldoAtmDTO respuesta = new SaldoAtmDTO();

        respuesta.setCuentaId(cuentaId);
        respuesta.setSaldo(saldo);

        return respuesta;
    }
}       