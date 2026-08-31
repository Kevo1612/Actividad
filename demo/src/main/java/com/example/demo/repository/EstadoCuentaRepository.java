package com.example.demo.repository;

import com.example.demo.model.EstadoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoCuentaRepository extends JpaRepository<EstadoCuenta, Long> {
}