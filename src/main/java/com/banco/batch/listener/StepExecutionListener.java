package com.banco.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Listener para monitorear la ejecución de steps individuales.
 */
@Slf4j
@Component
public class StepExecutionListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        String stepName = stepExecution.getStepName();
        LocalDateTime startTime = LocalDateTime.ofInstant(
            stepExecution.getStartTime().toInstant(), 
            ZoneId.systemDefault()
        );
        
        log.info("--- Iniciando Step: {} ---", stepName);
        log.info("Hora de inicio del step: {}", startTime);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        String stepName = stepExecution.getStepName();
        LocalDateTime startTime = LocalDateTime.ofInstant(
            stepExecution.getStartTime().toInstant(), 
            ZoneId.systemDefault()
        );
        LocalDateTime endTime = LocalDateTime.ofInstant(
            stepExecution.getEndTime().toInstant(), 
            ZoneId.systemDefault()
        );
        
        Duration duration = Duration.between(startTime, endTime);
        
        log.info("--- Finalizando Step: {} ---", stepName);
        log.info("Estado: {}", stepExecution.getStatus());
        log.info("Duración: {} segundos", duration.getSeconds());
        log.info("Registros leídos: {}", stepExecution.getReadCount());
        log.info("Registros escritos: {}", stepExecution.getWriteCount());
        log.info("Registros omitidos: {}", stepExecution.getSkipCount());
        log.info("Commits: {}", stepExecution.getCommitCount());
        log.info("Rollbacks: {}", stepExecution.getRollbackCount());
        
        // Calcular throughput
        if (duration.getSeconds() > 0) {
            double throughput = (double) stepExecution.getReadCount() / duration.getSeconds();
            log.info("Throughput: {:.2f} registros/segundo", throughput);
        }
        
        // Log de errores específicos del step
        if (!stepExecution.getFailureExceptions().isEmpty()) {
            log.warn("Errores en el step {}:", stepName);
            stepExecution.getFailureExceptions().forEach(exception -> 
                log.warn("Error en step: {}", exception.getMessage())
            );
        }
        
        return stepExecution.getExitStatus();
    }
}
