package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.math.BigDecimal;

@Entity
@Table(name = "CUENTAS_INTERESES")
public class Interes {

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

    public Interes() {
    }

    public Interes(Long cuentaId, String nombre, BigDecimal saldoInicial,
                   Integer edad, String tipo, BigDecimal interes,
                   BigDecimal saldoFinal) {
        this.cuentaId = cuentaId;
        this.nombre = nombre;
        this.saldoInicial = saldoInicial;
        this.edad = edad;
        this.tipo = tipo;
        this.interes = interes;
        this.saldoFinal = saldoFinal;
    }

    public Long getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(Long cuentaId) {
        this.cuentaId = cuentaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public void setInteres(BigDecimal interes) {
        this.interes = interes;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }
}