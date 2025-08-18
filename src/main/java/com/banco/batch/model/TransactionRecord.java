package com.banco.batch.model;

import lombok.*;

/**
 * DTO de lectura del CSV legacy (formato del sistema legacy del banco).
 * Mapea los campos: id, fecha, monto, tipo
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransactionRecord {
    private String id;        // ID de transacción del sistema legacy
    private String fecha;     // Fecha en formato YYYY-MM-DD o YYYY/MM/DD
    private String monto;     // Monto como string para validar/parsear
    private String tipo;      // Tipo: debito, credito, etc.

    // Métodos de compatibilidad para el processor existente
    public String getAccountNumber() {
        return this.id; // Usar ID como número de cuenta por ahora
    }

    public String getTxnDate() {
        return this.fecha;
    }

    public String getAmount() {
        return this.monto;
    }

    public String getCategory() {
        return this.tipo;
    }
}
