package com.banco.batch.processor;

import com.banco.batch.model.Account;
import com.banco.batch.model.InterestLedger;
import lombok.Setter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Calcula interés mensual simple: interest = balance * (tasaAnual/12).
 */
@Component
public class MonthlyInterestProcessor implements ItemProcessor<Account, InterestLedger> {

    @Setter
    private String periodYyyymm = "2025-08";

    @Override
    public InterestLedger process(Account acc) {
        double rate = (acc.getAnnualInterestRate() == null ? 0.0 : acc.getAnnualInterestRate()) / 12.0;
        double interest = round2(acc.getBalance() * rate);
        double newBalance = round2(acc.getBalance() + interest);

        return InterestLedger.builder()
                .accountNumber(acc.getAccountNumber())
                .periodYyyymm(periodYyyymm)
                .interestAmount(interest)
                .newBalance(newBalance)
                .build();
    }

    private double round2(double v){ return Math.round(v * 100.0) / 100.0; }
}
