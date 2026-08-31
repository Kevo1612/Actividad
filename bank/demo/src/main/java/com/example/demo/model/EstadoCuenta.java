package com.example.demo.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ESTADOS_CUENTA")
public class EstadoCuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CUENTA_ID", nullable = false)
    private Long cuentaId;

    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @Column(name = "TOTAL_DEPOSITOS", precision = 15, scale = 2)
    private BigDecimal totalDepositos = BigDecimal.ZERO;

    @Column(name = "TOTAL_RETIROS", precision = 15, scale = 2)
    private BigDecimal totalRetiros = BigDecimal.ZERO;

    @Column(name = "TOTAL_COMPRAS", precision = 15, scale = 2)
    private BigDecimal totalCompras = BigDecimal.ZERO;

    @Column(name = "TOTAL_MOVIMIENTOS", precision = 15, scale = 2)
    private BigDecimal totalMovimientos = BigDecimal.ZERO;

    @Column(name = "CANTIDAD_MOVIMIENTOS")
    private Integer cantidadMovimientos = 0;

    public EstadoCuenta() {
    }

    public EstadoCuenta(Long cuentaId, Integer anio,
                        BigDecimal totalDepositos,
                        BigDecimal totalRetiros,
                        BigDecimal totalCompras,
                        BigDecimal totalMovimientos,
                        Integer cantidadMovimientos) {
        this.cuentaId = cuentaId;
        this.anio = anio;
        this.totalDepositos = totalDepositos;
        this.totalRetiros = totalRetiros;
        this.totalCompras = totalCompras;
        this.totalMovimientos = totalMovimientos;
        this.cantidadMovimientos = cantidadMovimientos;
    }

    public Long getId() {
        return id;
    }

    public Long getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(Long cuentaId) {
        this.cuentaId = cuentaId;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public BigDecimal getTotalDepositos() {
        return totalDepositos;
    }

    public void setTotalDepositos(BigDecimal totalDepositos) {
        this.totalDepositos = totalDepositos;
    }

    public BigDecimal getTotalRetiros() {
        return totalRetiros;
    }

    public void setTotalRetiros(BigDecimal totalRetiros) {
        this.totalRetiros = totalRetiros;
    }

    public BigDecimal getTotalCompras() {
        return totalCompras;
    }

    public void setTotalCompras(BigDecimal totalCompras) {
        this.totalCompras = totalCompras;
    }

    public BigDecimal getTotalMovimientos() {
        return totalMovimientos;
    }

    public void setTotalMovimientos(BigDecimal totalMovimientos) {
        this.totalMovimientos = totalMovimientos;
    }

    public Integer getCantidadMovimientos() {
        return cantidadMovimientos;
    }

    public void setCantidadMovimientos(Integer cantidadMovimientos) {
        this.cantidadMovimientos = cantidadMovimientos;
    }
}