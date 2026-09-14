package com.example.bancoxyz.service;

import com.example.bancoxyz.client.CuentaBackendClient;
import com.example.bancoxyz.dto.CuentaBackendDTO;
import com.example.bancoxyz.dto.SaldoMobileDTO;
import org.springframework.stereotype.Service;

@Service
public class SaldoMobileService {

    private final CuentaBackendClient cuentaBackendClient;

    public SaldoMobileService(CuentaBackendClient cuentaBackendClient) {
        this.cuentaBackendClient = cuentaBackendClient;
    }

    public SaldoMobileDTO obtenerSaldo(Long cuentaId) {

        CuentaBackendDTO cuenta =
                cuentaBackendClient.obtenerCuenta(cuentaId);

        SaldoMobileDTO saldo = new SaldoMobileDTO();

        saldo.setCuentaId(cuenta.getCuentaId());
        saldo.setSaldo(cuenta.getSaldoFinal());

        return saldo;
    }
}