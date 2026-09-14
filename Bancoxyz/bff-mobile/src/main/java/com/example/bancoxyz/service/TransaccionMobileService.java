package com.example.bancoxyz.service;

import com.example.bancoxyz.client.TransaccionBackendClient;
import com.example.bancoxyz.dto.TransaccionBackendDTO;
import com.example.bancoxyz.dto.TransaccionMobileDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransaccionMobileService {

    private final TransaccionBackendClient transaccionBackendClient;

    public TransaccionMobileService(
            TransaccionBackendClient transaccionBackendClient) {

        this.transaccionBackendClient = transaccionBackendClient;
    }

    public List<TransaccionMobileDTO> obtenerUltimasTransacciones() {

        List<TransaccionBackendDTO> transacciones =
                transaccionBackendClient.obtenerTransacciones();

        return transacciones.stream()
                .limit(5)
                .map(transaccion -> {
                    TransaccionMobileDTO dto =
                            new TransaccionMobileDTO();

                    dto.setFecha(transaccion.getFecha());
                    dto.setMonto(transaccion.getMonto());
                    dto.setTipo(transaccion.getTipo());

                    return dto;
                })
                .toList();
    }
}