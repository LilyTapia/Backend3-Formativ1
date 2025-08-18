package com.banco.batch.model;

import lombok.*;

/**
 * DTO de lectura del CSV de intereses legacy.
 * Mapea los campos: cuenta_id, nombre, saldo, edad, tipo
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InterestRecord {
    private String cuentaId;    // ID de cuenta del sistema legacy
    private String nombre;      // Nombre del titular
    private String saldo;       // Saldo como string para validar/parsear
    private String edad;        // Edad como string para validar
    private String tipo;        // Tipo de cuenta: ahorro, prestamo, hipoteca
}
