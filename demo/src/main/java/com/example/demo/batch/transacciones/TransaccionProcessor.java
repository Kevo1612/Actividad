package com.example.demo.batch.transacciones;

import com.example.demo.dto.TransaccionCsv;
import com.example.demo.model.Transaccion;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
public class TransaccionProcessor implements ItemProcessor<TransaccionCsv, Transaccion> {

    private static final List<DateTimeFormatter> FORMATOS_FECHA = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
    );

    @Override
    public Transaccion process(TransaccionCsv item) throws Exception {

        if (item.getId() == null || item.getId().isBlank()) {
            throw new IllegalArgumentException("ID vacío");
        }

        if (item.getFecha() == null || item.getFecha().isBlank()) {
            throw new IllegalArgumentException("Fecha vacía");
        }

        if (item.getMonto() == null || item.getMonto().isBlank()) {
            throw new IllegalArgumentException("Monto vacío");
        }

        if (item.getTipo() == null || item.getTipo().isBlank()) {
            throw new IllegalArgumentException("Tipo vacío");
        }

        Long id;

        try {
            id = Long.parseLong(item.getId().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID inválido: " + item.getId());
        }

        LocalDate fecha = convertirFecha(item.getFecha().trim());

        BigDecimal monto;

        try {
            monto = new BigDecimal(item.getMonto().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Monto inválido: " + item.getMonto());
        }

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }

        String tipo = item.getTipo().trim().toLowerCase();

        if (!tipo.equals("credito") && !tipo.equals("debito")) {
            throw new IllegalArgumentException("Tipo inválido: " + tipo);
        }

        return new Transaccion(id, fecha, monto, tipo);
    }

    private LocalDate convertirFecha(String fecha) {

        for (DateTimeFormatter formato : FORMATOS_FECHA) {
            try {
                return LocalDate.parse(fecha, formato);
            } catch (DateTimeParseException ignored) {
            }
        }

        throw new IllegalArgumentException("Fecha inválida: " + fecha);
    }
}