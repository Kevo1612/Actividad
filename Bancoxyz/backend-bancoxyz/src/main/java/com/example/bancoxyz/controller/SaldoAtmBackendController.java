package com.example.bancoxyz.controller;

import com.example.bancoxyz.service.SaldoAtmBackendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/backend/atm/cuentas")
public class SaldoAtmBackendController {

    private final SaldoAtmBackendService saldoAtmBackendService;

    public SaldoAtmBackendController(
            SaldoAtmBackendService saldoAtmBackendService) {

        this.saldoAtmBackendService = saldoAtmBackendService;
    }

    @GetMapping("/{id}/saldo")
    public ResponseEntity<BigDecimal> obtenerSaldo(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                saldoAtmBackendService.obtenerSaldo(id)
        );
    }
}