package com.banco.batch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Carga nombres de archivo y columnas reales del CSV legacy desde application.yml.
 */
@ConfigurationProperties(prefix = "legacy")
@Getter @Setter
public class LegacyCsvProperties {

    private Files files = new Files();
    private Columns columns = new Columns();

    @Getter @Setter
    public static class Files {
        /** Nombre del CSV diario (copiado desde bank_legacy_data a /resources/data) */
        private String dailyTransactionsFile;
        private String interestFile;
        private String annualAccountsFile;
    }

    @Getter @Setter
    public static class Columns {
        /** Nombres REALES de las columnas en el CSV legacy */
        private String id;           // "id"
        private String fecha;        // "fecha"
        private String monto;        // "monto"
        private String tipo;         // "tipo"
    }
}
