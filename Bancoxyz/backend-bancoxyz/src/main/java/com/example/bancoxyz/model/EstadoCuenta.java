package com.example.bancoxyz.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "ESTADOS_CUENTA")
@Getter
@Setter
public class EstadoCuenta {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "CUENTA_ID")
    private Long cuentaId;

    @Column(name = "ANIO")
    private Integer anio;

    @Column(name = "TOTAL_DEPOSITOS")
    private BigDecimal totalDepositos;

    @Column(name = "TOTAL_RETIROS")
    private BigDecimal totalRetiros;

    @Column(name = "TOTAL_COMPRAS")
    private BigDecimal totalCompras;

    @Column(name = "TOTAL_MOVIMIENTOS")
    private BigDecimal totalMovimientos;

    @Column(name = "CANTIDAD_MOVIMIENTOS")
    private Integer cantidadMovimientos;
}