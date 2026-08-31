package com.example.demo.batch;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransaccionJobConfig {

    @Bean
    public Job transaccionJob(
            JobRepository jobRepository,
            Step transaccionStep) {

        return new JobBuilder("transaccionJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(transaccionStep)
                .build();
    }
}