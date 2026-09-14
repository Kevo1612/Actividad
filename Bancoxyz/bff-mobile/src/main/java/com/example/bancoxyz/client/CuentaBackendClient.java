package com.example.bancoxyz.client;

import com.example.bancoxyz.dto.CuentaBackendDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CuentaBackendClient {

    private final RestClient restClient;

    public CuentaBackendClient(
            RestClient.Builder restClientBuilder,
            @Value("${backend.base-url}") String backendBaseUrl) {

        this.restClient = restClientBuilder
                .baseUrl(backendBaseUrl)
                .build();
    }

    public CuentaBackendDTO obtenerCuenta(Long id) {

        return restClient.get()
                .uri("/api/backend/cuentas/{id}", id)
                .retrieve()
                .body(CuentaBackendDTO.class);
    }
}