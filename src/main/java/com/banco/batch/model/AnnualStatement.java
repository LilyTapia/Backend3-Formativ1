package com.banco.batch.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Resumen anual por cuenta.
 */
@Entity @Table(name = "annual_statement")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AnnualStatement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "`year`")
    private Integer year;

    @Column(name = "total_deposits")
    private Double totalDeposits;

    @Column(name = "total_withdrawals")
    private Double totalWithdrawals; // valor absoluto

    @Column(name = "end_balance")
    private Double endBalance;
}
