package com.banco.batch.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para habilitar las propiedades de configuración.
 */
@Configuration
@EnableConfigurationProperties(LegacyCsvProperties.class)
public class PropertiesConfig {
}
