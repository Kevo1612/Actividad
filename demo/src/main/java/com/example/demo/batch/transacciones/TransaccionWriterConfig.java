package com.example.demo.batch.transacciones;

import com.example.demo.model.Transaccion;
import com.example.demo.repository.TransaccionRepository;
import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransaccionWriterConfig {

    @Bean
    public RepositoryItemWriter<Transaccion> transaccionWriter(
            TransaccionRepository repository) {

        RepositoryItemWriter<Transaccion> writer =
                new RepositoryItemWriter<>(repository);

        writer.setMethodName("save");

        return writer;
    }
}