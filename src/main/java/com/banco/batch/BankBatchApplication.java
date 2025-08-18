package com.banco.batch;

import com.banco.batch.config.LegacyCsvProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Punto de entrada de la app Spring Boot.
 * No lanza jobs automáticamente (spring.batch.job.enabled=false).
 * Los jobs se lanzan vía REST (JobController) o desde tu IDE.
 */
@SpringBootApplication
@EnableConfigurationProperties(LegacyCsvProperties.class)
public class BankBatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankBatchApplication.class, args);
    }
}
