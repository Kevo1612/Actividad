package com.example.bancoxyz.controller;

import com.example.bancoxyz.model.EstadoCuenta;
import com.example.bancoxyz.service.EstadoCuentaBackendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/backend/estado-cuenta")
public class EstadoCuentaBackendController {

    private final EstadoCuentaBackendService estadoCuentaBackendService;

    public EstadoCuentaBackendController(
            EstadoCuentaBackendService estadoCuentaBackendService) {
        this.estadoCuentaBackendService = estadoCuentaBackendService;
    }

    @GetMapping("/{cuentaId}")
    public ResponseEntity<List<EstadoCuenta>> obtenerEstadosPorCuenta(
            @PathVariable Long cuentaId) {

        return ResponseEntity.ok(
                estadoCuentaBackendService.obtenerEstadosPorCuenta(cuentaId)
        );
    }
}