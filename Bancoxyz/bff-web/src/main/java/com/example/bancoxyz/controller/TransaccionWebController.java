package com.example.bancoxyz.controller;

import com.example.bancoxyz.dto.TransaccionWebDTO;
import com.example.bancoxyz.service.TransaccionWebService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/web/transacciones")
public class TransaccionWebController {

    private final TransaccionWebService transaccionWebService;

    public TransaccionWebController(TransaccionWebService transaccionWebService) {
        this.transaccionWebService = transaccionWebService;
    }

    @GetMapping
    public ResponseEntity<List<TransaccionWebDTO>> obtenerTransacciones() {

        return ResponseEntity.ok(
                transaccionWebService.obtenerTransacciones()
        );
    }
}