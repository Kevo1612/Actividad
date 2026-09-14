package com.example.bancoxyz.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class RetiroAtmResponseDTO {

    private BigDecimal montoRetirado;

    private BigDecimal saldoDisponible;

    private String mensaje;
}