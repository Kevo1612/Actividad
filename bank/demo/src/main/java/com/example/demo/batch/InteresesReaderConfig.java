package com.example.demo.batch;

import com.example.demo.dto.InteresCsv;
import org.springframework.batch.infrastructure.item.ItemStreamReader;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class InteresesReaderConfig {

    private FlatFileItemReader<InteresCsv> flatFileReader() {

        return new FlatFileItemReaderBuilder<InteresCsv>()
                .name("interesesReader")
                .resource(new ClassPathResource("data/intereses.csv"))
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names("cuentaId", "nombre", "saldo", "edad", "tipo")
                .targetType(InteresCsv.class)
                .build();
    }

    // Igual que en TransaccionReaderConfig: el step corre multi-thread, y
    // FlatFileItemReader no es thread-safe, así que se sincroniza el acceso
    // a read() envolviéndolo con SynchronizedItemStreamReader.
    @Bean
    public ItemStreamReader<InteresCsv> interesesReader() {

        return new SynchronizedItemStreamReaderBuilder<InteresCsv>()
                .delegate(flatFileReader())
                .build();
    }
}