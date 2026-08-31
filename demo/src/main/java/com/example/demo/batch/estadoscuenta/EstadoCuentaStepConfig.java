package com.example.demo.batch.estadoscuenta;

import com.example.demo.dto.EstadoCuentaCsv;
import com.example.demo.model.EstadoCuenta;

import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class EstadoCuentaStepConfig {

    @Bean
    public Step estadosCuentaStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<EstadoCuentaCsv> estadoCuentaReader,
            ItemProcessor<EstadoCuentaCsv, EstadoCuenta> estadoCuentaProcessor,
            ItemWriter<EstadoCuenta> estadoCuentaWriter,
            ThreadPoolTaskExecutor batchTaskExecutor) {

        return new StepBuilder("estadosCuentaStep", jobRepository)
                .<EstadoCuentaCsv, EstadoCuenta>chunk(10)
                .transactionManager(transactionManager)
                .reader(estadoCuentaReader)
                .processor(estadoCuentaProcessor)
                .writer(estadoCuentaWriter)
                // Mismo criterio de escalamiento que transaccionStep: step
                // multi-thread con el executor acotado (BatchTaskExecutorConfig),
                // en vez de particiones, dado que la fuente es un único archivo.
                .taskExecutor(batchTaskExecutor)
                .faultTolerant()
                .skip(IllegalArgumentException.class)
                .skipLimit(1000)
                .build();
    }
}