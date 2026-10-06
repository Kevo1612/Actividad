package com.example.bancoxyz.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "CUENTAS_INTERESES")
@Getter
@Setter
public class CuentaInteres {

    @Id
    @Column(name = "CUENTA_ID")
    private Long cuentaId;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "SALDO_INICIAL")
    private BigDecimal saldoInicial;

    @Column(name = "EDAD")
    private Integer edad;

    @Column(name = "TIPO")
    private String tipo;

    @Column(name = "INTERES")
    private BigDecimal interes;

    @Column(name = "SALDO_FINAL")
    private BigDecimal saldoFinal;
}