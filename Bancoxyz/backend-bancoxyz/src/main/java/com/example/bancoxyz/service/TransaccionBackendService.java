package com.example.bancoxyz.service;

import com.example.bancoxyz.model.Transaccion;
import com.example.bancoxyz.repository.TransaccionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransaccionBackendService {

    private final TransaccionRepository transaccionRepository;

    public TransaccionBackendService(
            TransaccionRepository transaccionRepository) {
        this.transaccionRepository = transaccionRepository;
    }

    public List<Transaccion> obtenerTransacciones() {
        return transaccionRepository.findAllByOrderByFechaDesc();
    }
}