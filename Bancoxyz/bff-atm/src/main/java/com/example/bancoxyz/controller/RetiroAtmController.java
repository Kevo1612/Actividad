package com.example.bancoxyz.controller;

import com.example.bancoxyz.dto.RetiroAtmRequestDTO;
import com.example.bancoxyz.dto.RetiroAtmResponseDTO;
import com.example.bancoxyz.service.RetiroAtmService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/atm/cuentas")
public class RetiroAtmController {

    private final RetiroAtmService retiroAtmService;

    public RetiroAtmController(RetiroAtmService retiroAtmService) {
        this.retiroAtmService = retiroAtmService;
    }

    @PostMapping("/{id}/retiro")
    public ResponseEntity<RetiroAtmResponseDTO> realizarRetiro(
            @PathVariable Long id,
            @Valid @RequestBody RetiroAtmRequestDTO request) {

        return ResponseEntity.ok(
                retiroAtmService.realizarRetiro(id, request)
        );
    }
}