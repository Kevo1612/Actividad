package com.example.demo.batch.estadoscuenta;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EstadosCuentaJobConfig {

    @Bean
    public Job estadosCuentaJob(
            JobRepository jobRepository,
            Step estadosCuentaStep) {

        return new JobBuilder("estadosCuentaJob", jobRepository)
                .start(estadosCuentaStep)
                .build();
    }
}