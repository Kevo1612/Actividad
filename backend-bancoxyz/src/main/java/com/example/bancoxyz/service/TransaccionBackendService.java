package com.example.bancoxyz.service;

import com.example.bancoxyz.model.Transaccion;
import com.example.bancoxyz.repository.TransaccionRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransaccionBackendService {

    private final TransaccionRepository transaccionRepository;

    public TransaccionBackendService(
            TransaccionRepository transaccionRepository) {
        this.transaccionRepository = transaccionRepository;
    }

    @CircuitBreaker(
            name = "transaccionesService",
            fallbackMethod = "obtenerTransaccionesFallback"
    )
    public List<Transaccion> obtenerTransacciones() {

        return transaccionRepository.findAllByOrderByFechaDesc();
    }

    public List<Transaccion> obtenerTransaccionesFallback(
            Throwable exception) {

        System.out.println(
                "Circuit Breaker activado: "
                        + exception.getMessage()
        );

        return List.of();
    }

    // PRUEBA CONTROLADA DE RESILIENCE4J
    @CircuitBreaker(
            name = "transaccionesService",
            fallbackMethod = "pruebaFalloFallback"
    )
    public String pruebaFalloControlado() {

        throw new RuntimeException(
                "Fallo controlado para probar Resilience4j"
        );
    }

    public String pruebaFalloFallback(Throwable exception) {

        return "FALLBACK ACTIVADO: Resilience4j interceptó el fallo. "
                + "Motivo: "
                + exception.getMessage();
    }
}