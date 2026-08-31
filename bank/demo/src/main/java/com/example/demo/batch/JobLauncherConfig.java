package com.example.demo.batch;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobLauncherConfig {

    @Bean
    public CommandLineRunner ejecutarJobs(
            JobLauncher jobLauncher,
            @Qualifier("transaccionJob") Job transaccionJob,
            @Qualifier("interesesJob") Job interesesJob,
            @Qualifier("estadosCuentaJob") Job estadosCuentaJob,
            ConfigurableApplicationContext context) {

        return args -> {

            // Job 1: Reporte de transacciones
            JobParameters parametrosTransacciones = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution ejecucionTransacciones =
                    jobLauncher.run(transaccionJob, parametrosTransacciones);

            // Job 2: Cálculo de intereses
            JobParameters parametrosIntereses = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis() + 1)
                    .toJobParameters();

            JobExecution ejecucionIntereses =
                    jobLauncher.run(interesesJob, parametrosIntereses);

            // Job 3: Estados de cuenta anuales
            JobParameters parametrosEstados = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis() + 2)
                    .toJobParameters();

            JobExecution ejecucionEstados =
                    jobLauncher.run(estadosCuentaJob, parametrosEstados);

            // Por qué esto es necesario:
            // batchTaskExecutor (ThreadPoolTaskExecutor) crea hilos NO-daemon
            // que quedan esperando en la cola de tareas incluso después de
            // que los 3 jobs terminan. Un CommandLineRunner por sí solo no
            // cierra el ApplicationContext al terminar, así que esos hilos
            // nunca reciben la señal de shutdown y el proceso se queda
            // colgado indefinidamente aunque los logs digan COMPLETED.
            //
            // Solución: cerrar el contexto explícitamente al terminar. Esto
            // dispara el ciclo de vida de destrucción de los beans, incluido
            // ThreadPoolTaskExecutor.destroy() (que sí llama a shutdown() y
            // respeta awaitTerminationSeconds configurado en
            // BatchTaskExecutorConfig), y luego se sale de la JVM con un
            // código de salida acorde al resultado de los jobs.
            boolean todosCompletados =
                    !ejecucionTransacciones.getStatus().isUnsuccessful()
                            && !ejecucionIntereses.getStatus().isUnsuccessful()
                            && !ejecucionEstados.getStatus().isUnsuccessful();

            int codigoSalida = todosCompletados ? 0 : 1;

            System.exit(SpringApplication.exit(context, () -> codigoSalida));
        };
    }
}