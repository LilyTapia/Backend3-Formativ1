package com.banco.batch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Carga nombres de archivo y columnas reales del CSV legacy desde application.yml.
 */
@Configuration
@ConfigurationProperties(prefix = "legacy")
@Getter @Setter
public class LegacyCsvProperties {

    private Files files = new Files();
    private Columns columns = new Columns();

    @Getter @Setter
    public static class Files {
        /** Nombre del CSV diario (copiado desde bank_legacy_data a /resources/data) */
        private String dailyTransactionsFile;
    }

    @Getter @Setter
    public static class Columns {
        /** Nombres REALES de las columnas en el CSV legacy */
        private String accountNumber;  // p.ej. "account_id"
        private String txnDate;        // p.ej. "date"
        private String amount;         // p.ej. "value"
        private String category;       // p.ej. "operation_type"
    }
}
