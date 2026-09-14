package com.example.bancoxyz.service;

import com.example.bancoxyz.client.CuentaBackendClient;
import com.example.bancoxyz.client.EstadoCuentaBackendClient;
import com.example.bancoxyz.client.TransaccionBackendClient;
import com.example.bancoxyz.dto.CuentaWebDTO;
import com.example.bancoxyz.dto.EstadoCuentaWebDTO;
import com.example.bancoxyz.dto.ResumenWebDTO;
import com.example.bancoxyz.dto.TransaccionWebDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResumenWebService {

    private final CuentaBackendClient cuentaBackendClient;
    private final EstadoCuentaBackendClient estadoCuentaBackendClient;
    private final TransaccionBackendClient transaccionBackendClient;

    public ResumenWebService(
            CuentaBackendClient cuentaBackendClient,
            EstadoCuentaBackendClient estadoCuentaBackendClient,
            TransaccionBackendClient transaccionBackendClient) {

        this.cuentaBackendClient = cuentaBackendClient;
        this.estadoCuentaBackendClient = estadoCuentaBackendClient;
        this.transaccionBackendClient = transaccionBackendClient;
    }

    public ResumenWebDTO obtenerResumen(Long cuentaId) {

        CuentaWebDTO cuenta =
                cuentaBackendClient.obtenerCuenta(cuentaId);

        List<EstadoCuentaWebDTO> estados =
                estadoCuentaBackendClient.obtenerEstadosPorCuenta(cuentaId);

        List<TransaccionWebDTO> transacciones =
                transaccionBackendClient.obtenerTransacciones();

        ResumenWebDTO resumen = new ResumenWebDTO();

        resumen.setCuenta(cuenta);
        resumen.setEstadosCuenta(estados);
        resumen.setTransacciones(transacciones);

        return resumen;
    }
}