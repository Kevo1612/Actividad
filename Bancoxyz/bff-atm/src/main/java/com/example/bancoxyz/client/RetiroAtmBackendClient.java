package com.example.bancoxyz.client;

import com.example.bancoxyz.dto.RetiroAtmRequestDTO;
import com.example.bancoxyz.dto.RetiroAtmResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RetiroAtmBackendClient {

    private final RestClient restClient;

    public RetiroAtmBackendClient(
            RestClient.Builder restClientBuilder,
            @Value("${backend.base-url}") String backendBaseUrl) {

        this.restClient = restClientBuilder
                .baseUrl(backendBaseUrl)
                .build();
    }

    public RetiroAtmResponseDTO realizarRetiro(
            Long cuentaId,
            RetiroAtmRequestDTO request) {

        return restClient.post()
                .uri("/api/backend/atm/cuentas/{id}/retiro", cuentaId)
                .body(request)
                .retrieve()
                .body(RetiroAtmResponseDTO.class);
    }
}