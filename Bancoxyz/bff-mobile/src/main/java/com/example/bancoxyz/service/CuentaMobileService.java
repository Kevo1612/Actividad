package com.example.bancoxyz.service;

import com.example.bancoxyz.client.CuentaBackendClient;
import com.example.bancoxyz.dto.CuentaBackendDTO;
import com.example.bancoxyz.dto.CuentaMobileDTO;
import org.springframework.stereotype.Service;

@Service
public class CuentaMobileService {

    private final CuentaBackendClient cuentaBackendClient;

    public CuentaMobileService(CuentaBackendClient cuentaBackendClient) {
        this.cuentaBackendClient = cuentaBackendClient;
    }

    public CuentaMobileDTO obtenerResumen(Long id) {

        CuentaBackendDTO cuenta =
                cuentaBackendClient.obtenerCuenta(id);

        CuentaMobileDTO resumen = new CuentaMobileDTO();

        resumen.setCuentaId(cuenta.getCuentaId());
        resumen.setNombre(cuenta.getNombre());
        resumen.setTipo(cuenta.getTipo());
        resumen.setSaldo(cuenta.getSaldoFinal());

        return resumen;
    }
}