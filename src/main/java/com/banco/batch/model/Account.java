package com.banco.batch.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Representa una cuenta bancaria.
 * type: SAVINGS o LOAN.
 * annualInterestRate: tasa anual (0.03 = 3%).
 */
@Entity @Table(name = "accounts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Double balance;

    @Column(nullable = false, name = "annual_interest_rate")
    private Double annualInterestRate;
}
