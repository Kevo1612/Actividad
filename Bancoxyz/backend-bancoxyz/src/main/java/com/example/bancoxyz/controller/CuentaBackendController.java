package com.example.bancoxyz.controller;

import com.example.bancoxyz.model.CuentaInteres;
import com.example.bancoxyz.service.CuentaBackendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backend/cuentas")
public class CuentaBackendController {

    private final CuentaBackendService cuentaBackendService;

    public CuentaBackendController(CuentaBackendService cuentaBackendService) {
        this.cuentaBackendService = cuentaBackendService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaInteres> obtenerCuenta(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                cuentaBackendService.obtenerCuenta(id)
        );
    }
}