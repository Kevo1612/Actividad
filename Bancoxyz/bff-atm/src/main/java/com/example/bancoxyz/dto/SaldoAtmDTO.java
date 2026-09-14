package com.example.bancoxyz.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SaldoAtmDTO {

    private Long cuentaId;
    private BigDecimal saldo;
}