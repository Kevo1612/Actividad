package com.example.demo.batch.transacciones;

import com.example.demo.dto.TransaccionCsv;

import org.springframework.batch.infrastructure.item.ItemStreamReader;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class TransaccionReaderConfig {

    private FlatFileItemReader<TransaccionCsv> flatFileReader() {

        return new FlatFileItemReaderBuilder<TransaccionCsv>()
                .name("transaccionReader")
                .resource(new ClassPathResource("data/transacciones.csv"))
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names("id", "fecha", "monto", "tipo")
                .targetType(TransaccionCsv.class)
                .build();
    }

    /**
     * FlatFileItemReader NO es thread-safe: su método read() avanza un
     * puntero interno sobre el archivo, y si dos hilos lo invocan a la vez
     * pueden leer líneas repetidas o saltarse líneas. Como el Step de
     * transacciones se ejecuta con un TaskExecutor multi-hilo, se envuelve
     * el reader en un SynchronizedItemStreamReader, que serializa las
     * llamadas a read() con un lock, dejando el resto del step (procesador
     * y writer) en paralelo real entre chunks.
     */
    @Bean
    public ItemStreamReader<TransaccionCsv> transaccionReader() {

        return new SynchronizedItemStreamReaderBuilder<TransaccionCsv>()
                .delegate(flatFileReader())
                .build();
    }
}