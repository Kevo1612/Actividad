package com.example.demo.batch;

import com.example.demo.dto.InteresCsv;
import com.example.demo.model.Interes;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class InteresesProcessor implements ItemProcessor<InteresCsv, Interes> {

    private static final BigDecimal TASA_AHORRO = new BigDecimal("0.01");
    private static final BigDecimal TASA_PRESTAMO = new BigDecimal("0.02");

    @Override
    public Interes process(InteresCsv item) throws Exception {

        // Validar ID de cuenta
        if (item.getCuentaId() == null || item.getCuentaId().isBlank()) {
            throw new IllegalArgumentException("Cuenta ID vacío");
        }

        Long cuentaId;

        try {
            cuentaId = Long.parseLong(item.getCuentaId().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Cuenta ID inválido: " + item.getCuentaId());
        }

        // Validar nombre
        if (item.getNombre() == null || item.getNombre().isBlank()) {
            throw new IllegalArgumentException("Nombre vacío");
        }

        // Validar saldo
        if (item.getSaldo() == null || item.getSaldo().isBlank()) {
            throw new IllegalArgumentException("Saldo vacío");
        }

        BigDecimal saldo;

        try {
            saldo = new BigDecimal(item.getSaldo().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Saldo inválido: " + item.getSaldo());
        }

        if (saldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El saldo no puede ser negativo");
        }

        // Validar edad
        if (item.getEdad() == null || item.getEdad().isBlank()) {
            throw new IllegalArgumentException("Edad vacía");
        }

        Integer edad;

        try {
            edad = Integer.parseInt(item.getEdad().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Edad inválida: " + item.getEdad());
        }

        if (edad < 0 || edad > 120) {
            throw new IllegalArgumentException(
                    "Edad fuera de rango: " + edad);
        }

        // Validar tipo
        if (item.getTipo() == null || item.getTipo().isBlank()) {
            throw new IllegalArgumentException("Tipo vacío");
        }

        String tipo = item.getTipo().trim().toLowerCase();

        BigDecimal tasa;

        if (tipo.equals("ahorro")) {
            tasa = TASA_AHORRO;
        } else if (tipo.equals("prestamo")) {
            tasa = TASA_PRESTAMO;
        } else {
            throw new IllegalArgumentException(
                    "Tipo de cuenta inválido: " + tipo);
        }

        // Calcular interés
        BigDecimal interes = saldo
                .multiply(tasa)
                .setScale(2, RoundingMode.HALF_UP);

        // Calcular saldo final
        BigDecimal saldoFinal;

        if (tipo.equals("ahorro")) {
            saldoFinal = saldo.add(interes);
        } else {
            saldoFinal = saldo.add(interes);
        }

        saldoFinal = saldoFinal.setScale(2, RoundingMode.HALF_UP);

        return new Interes(
                cuentaId,
                item.getNombre().trim(),
                saldo.setScale(2, RoundingMode.HALF_UP),
                edad,
                tipo,
                interes,
                saldoFinal
        );
    }
}