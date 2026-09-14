package com.example.bancoxyz.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class SaldoAtmBackendClient {

    private final RestClient restClient;

    public SaldoAtmBackendClient(
            RestClient.Builder restClientBuilder,
            @Value("${backend.base-url}") String backendBaseUrl) {

        this.restClient = restClientBuilder
                .baseUrl(backendBaseUrl)
                .build();
    }

    public BigDecimal obtenerSaldo(Long cuentaId) {

        return restClient.get()
                .uri("/api/backend/atm/cuentas/{id}/saldo", cuentaId)
                .retrieve()
                .body(BigDecimal.class);
    }
}