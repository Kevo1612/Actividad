package com.example.demo.batch.estadoscuenta;

import com.example.demo.model.EstadoCuenta;
import com.example.demo.repository.EstadoCuentaRepository;

import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EstadoCuentaWriterConfig {

    @Bean
    public RepositoryItemWriter<EstadoCuenta> estadoCuentaWriter(
            EstadoCuentaRepository repository) {

        RepositoryItemWriter<EstadoCuenta> writer =
                new RepositoryItemWriter<>(repository);

        writer.setMethodName("save");

        return writer;
    }
}