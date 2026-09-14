package com.example.bancoxyz.client;

import com.example.bancoxyz.dto.EstadoCuentaWebDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Component
public class EstadoCuentaBackendClient {

    private final RestClient restClient;

    public EstadoCuentaBackendClient(
            RestClient.Builder restClientBuilder,
            @Value("${backend.base-url}") String backendBaseUrl) {

        this.restClient = restClientBuilder
                .baseUrl(backendBaseUrl)
                .build();
    }

    public List<EstadoCuentaWebDTO> obtenerEstadosPorCuenta(Long cuentaId) {

        EstadoCuentaWebDTO[] respuesta = restClient.get()
                .uri("/api/backend/estado-cuenta/{cuentaId}", cuentaId)
                .retrieve()
                .body(EstadoCuentaWebDTO[].class);

        return respuesta != null
                ? Arrays.asList(respuesta)
                : List.of();
    }
}