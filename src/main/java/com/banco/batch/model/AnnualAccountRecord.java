package com.banco.batch.model;

import lombok.*;

/**
 * DTO de lectura del CSV de cuentas anuales legacy.
 * Mapea los campos: cuenta_id, fecha, transaccion, monto, descripcion
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AnnualAccountRecord {
    private String cuentaId;      // ID de cuenta del sistema legacy
    private String fecha;         // Fecha de la transacción
    private String transaccion;   // Tipo de transacción: deposito, retiro, compra
    private String monto;         // Monto como string para validar/parsear
    private String descripcion;   // Descripción de la transacción
}
