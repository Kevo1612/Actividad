package com.example.bancoxyz.controller;

import com.example.bancoxyz.dto.RetiroAtmBackendRequestDTO;
import com.example.bancoxyz.dto.RetiroAtmBackendResponseDTO;
import com.example.bancoxyz.service.RetiroAtmBackendService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backend/atm/cuentas")
public class RetiroAtmBackendController {

    private final RetiroAtmBackendService retiroAtmBackendService;

    public RetiroAtmBackendController(
            RetiroAtmBackendService retiroAtmBackendService) {

        this.retiroAtmBackendService = retiroAtmBackendService;
    }

    @PostMapping("/{id}/retiro")
    public ResponseEntity<RetiroAtmBackendResponseDTO> realizarRetiro(
            @PathVariable Long id,
            @Valid @RequestBody RetiroAtmBackendRequestDTO request) {

        return ResponseEntity.ok(
                retiroAtmBackendService.realizarRetiro(id, request)
        );
    }
}