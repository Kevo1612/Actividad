package com.example.bancoxyz.controller;

import com.example.bancoxyz.dto.CuentaWebDTO;
import com.example.bancoxyz.service.CuentaWebService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/web/cuentas")
public class CuentaWebController {

    private final CuentaWebService cuentaWebService;

    public CuentaWebController(CuentaWebService cuentaWebService) {
        this.cuentaWebService = cuentaWebService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaWebDTO> obtenerCuenta(@PathVariable Long id) {

        return ResponseEntity.ok(
                cuentaWebService.obtenerCuenta(id)
        );
    }
}