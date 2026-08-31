package com.example.demo.batch;

import com.example.demo.dto.EstadoCuentaCsv;
import com.example.demo.model.EstadoCuenta;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
public class EstadoCuentaProcessor
        implements ItemProcessor<EstadoCuentaCsv, EstadoCuenta> {

    private static final List<DateTimeFormatter> FORMATOS_FECHA = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
    );

    @Override
    public EstadoCuenta process(EstadoCuentaCsv item) throws Exception {

        // Validar cuenta
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

        // Validar fecha
        if (item.getFecha() == null || item.getFecha().isBlank()) {
            throw new IllegalArgumentException("Fecha vacía");
        }

        LocalDate fecha = convertirFecha(item.getFecha().trim());

        // Validar tipo de transacción
        if (item.getTransaccion() == null ||
                item.getTransaccion().isBlank()) {

            throw new IllegalArgumentException("Tipo de transacción vacío");
        }

        String transaccion = item.getTransaccion()
                .trim()
                .toLowerCase();

        if (!transaccion.equals("deposito")
                && !transaccion.equals("retiro")
                && !transaccion.equals("compra")) {

            throw new IllegalArgumentException(
                    "Tipo de transacción inválido: " + transaccion);
        }

        // Validar monto
        if (item.getMonto() == null || item.getMonto().isBlank()) {
            throw new IllegalArgumentException("Monto vacío");
        }

        BigDecimal monto;

        try {
            monto = new BigDecimal(item.getMonto().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Monto inválido: " + item.getMonto());
        }

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero");
        }

        /*
         * Por ahora generamos un EstadoCuenta individual.
         * El agrupamiento anual lo realizaremos posteriormente
         * mediante el procesamiento del Job.
         */
        BigDecimal depositos = BigDecimal.ZERO;
        BigDecimal retiros = BigDecimal.ZERO;
        BigDecimal compras = BigDecimal.ZERO;

        switch (transaccion) {
            case "deposito" -> depositos = monto;
            case "retiro" -> retiros = monto;
            case "compra" -> compras = monto;
        }

        BigDecimal total = monto;

        return new EstadoCuenta(
                cuentaId,
                fecha.getYear(),
                depositos,
                retiros,
                compras,
                total,
                1
        );
    }

    private LocalDate convertirFecha(String fecha) {

        for (DateTimeFormatter formato : FORMATOS_FECHA) {
            try {
                return LocalDate.parse(fecha, formato);
            } catch (DateTimeParseException ignored) {
            }
        }

        throw new IllegalArgumentException(
                "Fecha inválida: " + fecha);
    }
}