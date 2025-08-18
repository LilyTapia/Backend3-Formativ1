package com.banco.batch.writer;

import com.banco.batch.model.InterestLedger;
import com.banco.batch.repository.AccountRepository;
import com.banco.batch.repository.InterestLedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * Guarda el ledger y actualiza saldo de cuentas.
 */
@Component
@RequiredArgsConstructor
public class MonthlyInterestWriter implements ItemWriter<InterestLedger> {

    private final InterestLedgerRepository ledgerRepository;
    private final AccountRepository accountRepository;

    @Override
    public void write(Chunk<? extends InterestLedger> chunk) {
        ledgerRepository.saveAll(chunk.getItems());
        chunk.getItems().forEach(l -> {
            accountRepository.findByAccountNumber(l.getAccountNumber()).ifPresent(acc -> {
                acc.setBalance(l.getNewBalance());
                accountRepository.save(acc);
            });
        });
    }
}
