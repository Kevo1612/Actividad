package com.example.bancoxyz.repository;

import com.example.bancoxyz.model.EstadoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstadoCuentaRepository
        extends JpaRepository<EstadoCuenta, Long> {

    List<EstadoCuenta> findByCuentaId(Long cuentaId);
}