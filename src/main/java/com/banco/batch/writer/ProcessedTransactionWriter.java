package com.banco.batch.writer;

import com.banco.batch.model.ProcessedTransaction;
import com.banco.batch.repository.ProcessedTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * Persiste en bloque las transacciones procesadas.
 */
@Component
@RequiredArgsConstructor
public class ProcessedTransactionWriter implements ItemWriter<ProcessedTransaction> {

    private final ProcessedTransactionRepository repository;

    @Override
    public void write(Chunk<? extends ProcessedTransaction> chunk) {
        repository.saveAll(chunk.getItems());
    }
}
