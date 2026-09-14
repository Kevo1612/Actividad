package com.example.bancoxyz.service;

import com.example.bancoxyz.client.RetiroAtmBackendClient;
import com.example.bancoxyz.dto.RetiroAtmRequestDTO;
import com.example.bancoxyz.dto.RetiroAtmResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class RetiroAtmService {

    private final RetiroAtmBackendClient retiroAtmBackendClient;

    public RetiroAtmService(
            RetiroAtmBackendClient retiroAtmBackendClient) {

        this.retiroAtmBackendClient = retiroAtmBackendClient;
    }

    public RetiroAtmResponseDTO realizarRetiro(
            Long cuentaId,
            RetiroAtmRequestDTO request) {

        return retiroAtmBackendClient.realizarRetiro(
                cuentaId,
                request
        );
    }
}