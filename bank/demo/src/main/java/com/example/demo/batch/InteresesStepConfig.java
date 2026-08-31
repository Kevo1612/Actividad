package com.example.demo.batch;

import com.example.demo.dto.InteresCsv;
import com.example.demo.model.Interes;

import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class InteresesStepConfig {

    @Bean
    public Step interesesStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<InteresCsv> interesesReader,
            ItemProcessor<InteresCsv, Interes> interesesProcessor,
            RepositoryItemWriter<Interes> interesesWriter,
            ThreadPoolTaskExecutor batchTaskExecutor) {

        return new StepBuilder("interesesStep", jobRepository)
                .<InteresCsv, Interes>chunk(10)
                .transactionManager(transactionManager)
                .reader(interesesReader)
                .processor(interesesProcessor)
                .writer(interesesWriter)
                // Mismo criterio de escalamiento que transaccionStep: step
                // multi-thread con el executor acotado (BatchTaskExecutorConfig),
                // en vez de particiones, dado que la fuente es un único archivo.
                .taskExecutor(batchTaskExecutor)
                .listener(new ChunkThreadLogger<Interes>("interesesStep"))
                .faultTolerant()
                .skip(IllegalArgumentException.class)
                .skipLimit(1000)
                .build();
    }
}