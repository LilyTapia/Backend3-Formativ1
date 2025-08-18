package com.banco.batch.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Libro mayor de intereses mensuales.
 */
@Entity @Table(name = "interest_ledger")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InterestLedger {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "period_yyyymm")
    private String periodYyyymm; // YYYY-MM

    @Column(name = "interest_amount")
    private Double interestAmount;

    @Column(name = "new_balance")
    private Double newBalance;
}
