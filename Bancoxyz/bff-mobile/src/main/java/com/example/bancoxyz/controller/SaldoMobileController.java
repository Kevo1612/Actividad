package com.example.bancoxyz.controller;

import com.example.bancoxyz.dto.SaldoMobileDTO;
import com.example.bancoxyz.service.SaldoMobileService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mobile/cuentas")
public class SaldoMobileController {

    private final SaldoMobileService saldoMobileService;

    public SaldoMobileController(SaldoMobileService saldoMobileService) {
        this.saldoMobileService = saldoMobileService;
    }

    @GetMapping("/{id}/saldo")
    public ResponseEntity<SaldoMobileDTO> obtenerSaldo(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                saldoMobileService.obtenerSaldo(id)
        );
    }
}