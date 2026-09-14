package com.example.bancoxyz.controller;

import com.example.bancoxyz.dto.EstadoCuentaWebDTO;
import com.example.bancoxyz.service.EstadoCuentaWebService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/web/estado-cuenta")
public class EstadoCuentaWebController {

    private final EstadoCuentaWebService estadoCuentaWebService;

    public EstadoCuentaWebController(
            EstadoCuentaWebService estadoCuentaWebService) {
        this.estadoCuentaWebService = estadoCuentaWebService;
    }

    @GetMapping("/{cuentaId}")
    public ResponseEntity<List<EstadoCuentaWebDTO>> obtenerEstadosCuenta(
            @PathVariable Long cuentaId) {

        return ResponseEntity.ok(
                estadoCuentaWebService.obtenerEstadosCuenta(cuentaId)
        );
    }
}