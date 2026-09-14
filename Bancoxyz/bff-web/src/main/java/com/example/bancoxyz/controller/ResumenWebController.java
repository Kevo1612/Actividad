package com.example.bancoxyz.controller;

import com.example.bancoxyz.dto.ResumenWebDTO;
import com.example.bancoxyz.service.ResumenWebService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/web/cuentas")
public class ResumenWebController {

    private final ResumenWebService resumenWebService;

    public ResumenWebController(ResumenWebService resumenWebService) {
        this.resumenWebService = resumenWebService;
    }

    @GetMapping("/{id}/resumen-completo")
    public ResponseEntity<ResumenWebDTO> obtenerResumenCompleto(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                resumenWebService.obtenerResumen(id)
        );
    }
}