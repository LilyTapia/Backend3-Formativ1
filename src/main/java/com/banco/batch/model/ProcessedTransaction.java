package com.banco.batch.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

/**
 * Resultado de procesar el CSV: incluye anomalías y mensaje explicativo.
 */
@Entity @Table(name = "processed_transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProcessedTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "txn_date")
    private Date txnDate;

    private Double amount;
    private String category;

    @Column(nullable = false)
    private Boolean anomaly;

    private String message;
}
