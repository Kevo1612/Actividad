package com.example.demo.batch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Política de escalamiento elegida: STEP MULTI-THREAD (no particionamiento).
 * Este mismo executor se reutiliza en los 3 steps del proyecto
 * (transaccionStep, interesesStep, estadosCuentaStep). Como JobLauncherConfig
 * lanza los jobs uno detrás del otro (no en paralelo), en todo momento como
 * mucho un solo step está usando el pool, así que no hace falta un pool por
 * job.
 *
 * Justificación:
 *  - Cada job de este proyecto lee un único archivo plano (transacciones.csv,
 *    intereses.csv, cuentas_anuales.csv). No existen múltiples recursos ni
 *    rangos naturales (por ejemplo, por rango de fechas o por archivo) sobre
 *    los cuales particionar sin introducir una lógica artificial de "corte"
 *    del archivo. Particionar aquí agregaría complejidad (un Partitioner,
 *    un PartitionHandler y N lectores independientes) sin beneficio real
 *    para volúmenes de ~1.000 registros por archivo.
 *  - Un Step multi-hilo (chunk-oriented step con un TaskExecutor) permite
 *    procesar varios chunks en paralelo reusando el mismo reader/writer,
 *    con una configuración mucho más simple y suficiente para este volumen.
 *
 * Parámetros del pool (justificados):
 *  - corePoolSize = 4 y maxPoolSize = 8: se asume una máquina con ~4-8 núcleos
 *    disponibles. El procesamiento es liviano (parseo + validación + insert
 *    JPA), por lo que no conviene sobredimensionar el pool: más hilos que
 *    núcleos físicos solo aumenta el cambio de contexto y la contención sobre
 *    la conexión a la base de datos, sin mejorar el throughput real.
 *  - queueCapacity = 50: con chunk(10) y ~1.000 registros por archivo hay
 *    ~100 chunks por job; una cola de 50 tareas evita que el executor cree
 *    hilos de más (por encima de maxPoolSize) mientras sigue absorbiendo
 *    ráfagas sin bloquear al hilo que lanza los chunks.
 *  - No se usa StepBuilder.throttleLimit(): está deprecado desde Spring
 *    Batch 5.0 y eliminado en Batch 6 (el que trae Spring Boot 4.1.1). El
 *    límite de concurrencia real de este step queda dado únicamente por
 *    este executor acotado (core/max pool size + cola limitada).
 */
@Configuration
public class BatchTaskExecutorConfig {

    private static final int CORE_POOL_SIZE = 4;
    private static final int MAX_POOL_SIZE = 8;
    private static final int QUEUE_CAPACITY = 50;

    @Bean
    public ThreadPoolTaskExecutor batchTaskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix("batch-worker-");

        // Si la cola y el maxPoolSize se llenan, el hilo que llama (el step)
        // ejecuta la tarea directamente en vez de rechazarla o perder datos.
        executor.setRejectedExecutionHandler(
                new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);

        return executor;
    }
}