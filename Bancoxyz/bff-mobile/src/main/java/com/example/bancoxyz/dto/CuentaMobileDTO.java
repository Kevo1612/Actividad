package com.example.bancoxyz.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CuentaMobileDTO {

    private Long cuentaId;
    private String nombre;
    private String tipo;
    private BigDecimal saldo;
}