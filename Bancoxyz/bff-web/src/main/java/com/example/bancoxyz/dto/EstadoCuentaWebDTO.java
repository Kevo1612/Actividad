package com.example.bancoxyz.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EstadoCuentaWebDTO {

    private Long id;
    private Long cuentaId;
    private Integer anio;
    private BigDecimal totalDepositos;
    private BigDecimal totalRetiros;
    private BigDecimal totalCompras;
    private BigDecimal totalMovimientos;
    private Integer cantidadMovimientos;
}