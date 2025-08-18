package com.banco.batch.processor;

import com.banco.batch.model.Account;
import com.banco.batch.model.AnnualStatement;
import com.banco.batch.repository.ProcessedTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Resume depósitos (>0) y retiros (<0) del año target para cada cuenta.
 */
@Component
@RequiredArgsConstructor
public class AnnualStatementProcessor implements ItemProcessor<Account, AnnualStatement> {

    private final ProcessedTransactionRepository txnRepo;

    @Setter
    private Integer targetYear = 2025;

    @Override
    public AnnualStatement process(Account acc) {
        var start = Date.valueOf(LocalDate.of(targetYear, 1, 1));
        var end   = Date.valueOf(LocalDate.of(targetYear, 12, 31));

        var txns = txnRepo.findByAccountNumberAndTxnDateBetween(
                acc.getAccountNumber(), start, end);

        double deposits = txns.stream().mapToDouble(t -> Math.max(0, t.getAmount())).sum();
        double withdrawals = txns.stream().mapToDouble(t -> Math.min(0, t.getAmount())).sum();

        return com.banco.batch.model.AnnualStatement.builder()
                .accountNumber(acc.getAccountNumber())
                .year(targetYear)
                .totalDeposits(round2(deposits))
                .totalWithdrawals(round2(Math.abs(withdrawals)))
                .endBalance(round2(acc.getBalance()))
                .build();
    }

    private double round2(double v){ return Math.round(v * 100.0) / 100.0; }
}
