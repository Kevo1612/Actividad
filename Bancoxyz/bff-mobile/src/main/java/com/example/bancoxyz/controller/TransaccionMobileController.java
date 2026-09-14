package com.example.bancoxyz.controller;

import com.example.bancoxyz.dto.TransaccionMobileDTO;
import com.example.bancoxyz.service.TransaccionMobileService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mobile/transacciones")
public class TransaccionMobileController {

    private final TransaccionMobileService transaccionMobileService;

    public TransaccionMobileController(
            TransaccionMobileService transaccionMobileService) {
        this.transaccionMobileService = transaccionMobileService;
    }

    @GetMapping("/ultimas")
    public ResponseEntity<List<TransaccionMobileDTO>> obtenerUltimasTransacciones() {

        return ResponseEntity.ok(
                transaccionMobileService.obtenerUltimasTransacciones()
        );
    }
}