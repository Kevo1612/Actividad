package com.example.bancoxyz.service;

import com.example.bancoxyz.client.TransaccionBackendClient;
import com.example.bancoxyz.dto.TransaccionWebDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransaccionWebService {

    private final TransaccionBackendClient transaccionBackendClient;

    public TransaccionWebService(
            TransaccionBackendClient transaccionBackendClient) {
        this.transaccionBackendClient = transaccionBackendClient;
    }

    public List<TransaccionWebDTO> obtenerTransacciones() {
        return transaccionBackendClient.obtenerTransacciones();
    }
}