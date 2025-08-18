package com.banco.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Listener para monitorear la ejecución de jobs y generar métricas.
 */
@Slf4j
@Component
public class JobExecutionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        LocalDateTime startTime = LocalDateTime.ofInstant(
            jobExecution.getStartTime().toInstant(), 
            ZoneId.systemDefault()
        );
        
        log.info("=== INICIANDO JOB: {} ===", jobName);
        log.info("Hora de inicio: {}", startTime);
        log.info("Parámetros: {}", jobExecution.getJobParameters().getParameters());
        log.info("ID de ejecución: {}", jobExecution.getId());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        LocalDateTime startTime = LocalDateTime.ofInstant(
            jobExecution.getStartTime().toInstant(), 
            ZoneId.systemDefault()
        );
        LocalDateTime endTime = LocalDateTime.ofInstant(
            jobExecution.getEndTime().toInstant(), 
            ZoneId.systemDefault()
        );
        
        Duration duration = Duration.between(startTime, endTime);
        
        log.info("=== FINALIZANDO JOB: {} ===", jobName);
        log.info("Estado final: {}", jobExecution.getStatus());
        log.info("Código de salida: {}", jobExecution.getExitStatus().getExitCode());
        log.info("Hora de inicio: {}", startTime);
        log.info("Hora de fin: {}", endTime);
        log.info("Duración total: {} segundos", duration.getSeconds());
        
        // Log de métricas por step
        jobExecution.getStepExecutions().forEach(stepExecution -> {
            log.info("Step '{}': Leídos={}, Procesados={}, Escritos={}, Omitidos={}, Errores={}", 
                stepExecution.getStepName(),
                stepExecution.getReadCount(),
                stepExecution.getProcessSkipCount() + stepExecution.getWriteCount(),
                stepExecution.getWriteCount(),
                stepExecution.getSkipCount(),
                stepExecution.getFailureExceptions().size()
            );
        });
        
        // Log de errores si los hay
        if (!jobExecution.getAllFailureExceptions().isEmpty()) {
            log.error("Errores durante la ejecución del job:");
            jobExecution.getAllFailureExceptions().forEach(exception -> 
                log.error("Error: {}", exception.getMessage(), exception)
            );
        }
        
        log.info("=== FIN DEL REPORTE DE JOB: {} ===", jobName);
    }
}
