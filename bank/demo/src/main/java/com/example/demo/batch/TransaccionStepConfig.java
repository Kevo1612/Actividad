package com.example.demo.batch;

import com.example.demo.model.Transaccion;
import com.example.demo.dto.TransaccionCsv;

import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransaccionStepConfig {

    @Bean
    public Step transaccionStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<TransaccionCsv> transaccionReader,
            ItemProcessor<TransaccionCsv, Transaccion> transaccionProcessor,
            ItemWriter<Transaccion> transaccionWriter,
            ThreadPoolTaskExecutor batchTaskExecutor) {

        return new StepBuilder("transaccionStep", jobRepository)
                .<TransaccionCsv, Transaccion>chunk(10)
                .transactionManager(transactionManager)
                .reader(transaccionReader)
                .processor(transaccionProcessor)
                .writer(transaccionWriter)
                // Step multi-thread: cada chunk (10 items) se ejecuta en un
                // hilo del pool. El límite de concurrencia real ya NO se fija
                // con throttleLimit (deprecado desde Batch 5.0 y removido en
                // Batch 6, que es lo que trae Spring Boot 4.1.1); en su lugar,
                // el propio ThreadPoolTaskExecutor acotado (core/max pool +
                // cola limitada, ver BatchTaskExecutorConfig) es quien limita
                // cuántos chunks corren en paralelo.
                .taskExecutor(batchTaskExecutor)
                .listener(new ChunkThreadLogger<Transaccion>("transaccionStep"))
                .faultTolerant()
                .skip(IllegalArgumentException.class)
                .skipLimit(1000)
                .build();
    }
}