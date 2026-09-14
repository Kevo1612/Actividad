package com.example.bancoxyz.service;

import com.example.bancoxyz.client.EstadoCuentaBackendClient;
import com.example.bancoxyz.dto.EstadoCuentaWebDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoCuentaWebService {

    private final EstadoCuentaBackendClient estadoCuentaBackendClient;

    public EstadoCuentaWebService(
            EstadoCuentaBackendClient estadoCuentaBackendClient) {
        this.estadoCuentaBackendClient = estadoCuentaBackendClient;
    }

    public List<EstadoCuentaWebDTO> obtenerEstadosCuenta(Long cuentaId) {
        return estadoCuentaBackendClient.obtenerEstadosPorCuenta(cuentaId);
    }
}