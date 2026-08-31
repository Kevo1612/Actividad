package com.example.demo.batch.estadoscuenta;

import com.example.demo.dto.EstadoCuentaCsv;
import org.springframework.batch.infrastructure.item.ItemStreamReader;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class EstadoCuentaReaderConfig {

    private FlatFileItemReader<EstadoCuentaCsv> flatFileReader() {

        return new FlatFileItemReaderBuilder<EstadoCuentaCsv>()
                .name("estadoCuentaReader")
                .resource(new ClassPathResource("data/cuentas_anuales.csv"))
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names("cuentaId", "fecha", "transaccion", "monto", "descripcion")
                .targetType(EstadoCuentaCsv.class)
                .build();
    }

    // Igual que en TransaccionReaderConfig: el step corre multi-thread, y
    // FlatFileItemReader no es thread-safe, así que se sincroniza el acceso
    // a read() envolviéndolo con SynchronizedItemStreamReader.
    @Bean
    public ItemStreamReader<EstadoCuentaCsv> estadoCuentaReader() {

        return new SynchronizedItemStreamReaderBuilder<EstadoCuentaCsv>()
                .delegate(flatFileReader())
                .build();
    }
}