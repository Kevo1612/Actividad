package com.example.bancoxyz.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class TransaccionWebDTO {

    private Long id;
    private LocalDate fecha;
    private BigDecimal monto;
    private String tipo;
}