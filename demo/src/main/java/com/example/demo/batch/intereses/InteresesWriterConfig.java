package com.example.demo.batch.intereses;

import com.example.demo.model.Interes;
import com.example.demo.repository.InteresRepository;

import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InteresesWriterConfig {

    @Bean
    public RepositoryItemWriter<Interes> interesesWriter(
            InteresRepository repository) {

        RepositoryItemWriter<Interes> writer =
                new RepositoryItemWriter<>(repository);

        writer.setMethodName("save");

        return writer;
    }
}