package com.example.bancoxyz.controller;

import com.example.bancoxyz.dto.SaldoAtmDTO;
import com.example.bancoxyz.service.SaldoAtmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/atm/cuentas")
public class SaldoAtmController {

    private final SaldoAtmService saldoAtmService;

    public SaldoAtmController(SaldoAtmService saldoAtmService) {
        this.saldoAtmService = saldoAtmService;
    }

    @GetMapping("/{id}/saldo")
    public ResponseEntity<SaldoAtmDTO> obtenerSaldo(@PathVariable Long id) {
        return ResponseEntity.ok(
                saldoAtmService.obtenerSaldo(id)
        );
    }
}