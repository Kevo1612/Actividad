package com.example.bancoxyz.controller;

import com.example.bancoxyz.model.Transaccion;
import com.example.bancoxyz.service.TransaccionBackendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/backend/transacciones")
public class TransaccionBackendController {

    private final TransaccionBackendService transaccionBackendService;

    public TransaccionBackendController(
            TransaccionBackendService transaccionBackendService) {
        this.transaccionBackendService = transaccionBackendService;
    }

    @GetMapping
    public ResponseEntity<List<Transaccion>> obtenerTransacciones() {

        return ResponseEntity.ok(
                transaccionBackendService.obtenerTransacciones()
        );
    }
}