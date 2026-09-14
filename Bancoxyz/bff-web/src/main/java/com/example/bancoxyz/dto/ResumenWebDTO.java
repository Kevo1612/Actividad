package com.example.bancoxyz.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResumenWebDTO {

    private CuentaWebDTO cuenta;

    private List<EstadoCuentaWebDTO> estadosCuenta;

    private List<TransaccionWebDTO> transacciones;
}