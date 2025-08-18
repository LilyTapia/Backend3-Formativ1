package com.banco.batch.config;

import org.springframework.context.annotation.Configuration;

/**
 * Ancla de configuración. Spring Boot autoconfigura JobRepository y
 * PlatformTransactionManager basados en la DataSource/JPA.
 */
@Configuration
public class BatchInfraConfig { }
