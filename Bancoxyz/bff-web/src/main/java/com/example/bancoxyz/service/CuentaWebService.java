package com.example.bancoxyz.service;

import com.example.bancoxyz.client.CuentaBackendClient;
import com.example.bancoxyz.dto.CuentaWebDTO;
import org.springframework.stereotype.Service;

@Service
public class CuentaWebService {

    private final CuentaBackendClient cuentaBackendClient;

    public CuentaWebService(CuentaBackendClient cuentaBackendClient) {
        this.cuentaBackendClient = cuentaBackendClient;
    }

    public CuentaWebDTO obtenerCuenta(Long id) {
        return cuentaBackendClient.obtenerCuenta(id);
    }
}