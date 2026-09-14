package com.example.bancoxyz.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ATM_OPERACIONES")
@Getter
@Setter
public class AtmOperacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CUENTA_ID", nullable = false)
    private Long cuentaId;

    @Column(name = "TIPO_OPERACION", nullable = false)
    private String tipoOperacion;

    @Column(name = "MONTO", nullable = false)
    private BigDecimal monto;

    @Column(name = "FECHA", nullable = false)
    private LocalDateTime fecha;
}