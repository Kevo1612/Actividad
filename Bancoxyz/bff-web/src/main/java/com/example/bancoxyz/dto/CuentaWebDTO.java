package com.example.bancoxyz.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CuentaWebDTO {

    private Long cuentaId;
    private String nombre;
    private Integer edad;
    private String tipo;
    private BigDecimal saldoInicial;
    private BigDecimal interes;
    private BigDecimal saldoFinal;
}