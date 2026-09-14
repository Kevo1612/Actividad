package com.example.bancoxyz.client;

import com.example.bancoxyz.dto.TransaccionWebDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Component
public class TransaccionBackendClient {

    private final RestClient restClient;

    public TransaccionBackendClient(
            RestClient.Builder restClientBuilder,
            @Value("${backend.base-url}") String backendBaseUrl) {

        this.restClient = restClientBuilder
                .baseUrl(backendBaseUrl)
                .build();
    }

    public List<TransaccionWebDTO> obtenerTransacciones() {

        TransaccionWebDTO[] respuesta = restClient.get()
                .uri("/api/backend/transacciones")
                .retrieve()
                .body(TransaccionWebDTO[].class);

        return respuesta != null
                ? Arrays.asList(respuesta)
                : List.of();
    }
}