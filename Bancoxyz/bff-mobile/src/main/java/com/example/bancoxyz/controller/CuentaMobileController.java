package com.example.bancoxyz.controller;

import com.example.bancoxyz.dto.CuentaMobileDTO;
import com.example.bancoxyz.service.CuentaMobileService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mobile/cuentas")
public class CuentaMobileController {

    private final CuentaMobileService cuentaMobileService;

    public CuentaMobileController(CuentaMobileService cuentaMobileService) {
        this.cuentaMobileService = cuentaMobileService;
    }

    @GetMapping("/{id}/resumen")
    public ResponseEntity<CuentaMobileDTO> obtenerResumen(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                cuentaMobileService.obtenerResumen(id)
        );
    }
}