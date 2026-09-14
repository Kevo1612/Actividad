package com.example.bancoxyz.repository;

import com.example.bancoxyz.model.AtmOperacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtmOperacionRepository
        extends JpaRepository<AtmOperacion, Long> {

    List<AtmOperacion> findByCuentaIdOrderByFechaDesc(Long cuentaId);
}