package com.example.demo.batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.ItemWriteListener;

import java.util.List;

/**
 * Log puntual, uno por chunk (no por item), que muestra qué hilo del pool
 * escribió ese chunk. Se usa como ItemWriteListener y no como ChunkListener
 * porque, según la documentación oficial de Spring Batch 6, "the ChunkListener
 * listener interface is not called in concurrent steps" — es decir, en un
 * step multi-thread ChunkListener simplemente no se dispara. ItemWriteListener
 * sí funciona porque afterWrite() se ejecuta en el mismo hilo que hizo el
 * write de ese chunk específico.
 */
public class ChunkThreadLogger<T> implements ItemWriteListener<T> {

    private static final Logger log = LoggerFactory.getLogger(ChunkThreadLogger.class);

    private final String stepName;

    public ChunkThreadLogger(String stepName) {
        this.stepName = stepName;
    }

    public void afterWrite(List<? extends T> items) {
        log.info("[{}] chunk de {} items escrito por el hilo: {}",
                stepName, items.size(), Thread.currentThread().getName());
    }
}
