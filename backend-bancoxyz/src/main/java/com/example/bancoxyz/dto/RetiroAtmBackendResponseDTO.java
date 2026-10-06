package com.example.bancoxyz.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class RetiroAtmBackendResponseDTO {

    private Long cuentaId;
    private BigDecimal montoRetirado;
    private BigDecimal saldoDisponible;
    private String tipoOperacion;
    private LocalDateTime fecha;
    private String mensaje;
}