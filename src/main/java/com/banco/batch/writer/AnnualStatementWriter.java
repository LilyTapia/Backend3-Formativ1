package com.banco.batch.writer;

import com.banco.batch.model.AnnualStatement;
import com.banco.batch.repository.AnnualStatementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * Persiste los estados de cuenta anuales generados.
 */
@Component
@RequiredArgsConstructor
public class AnnualStatementWriter implements ItemWriter<AnnualStatement> {

    private final AnnualStatementRepository repository;

    @Override
    public void write(Chunk<? extends AnnualStatement> chunk) {
        repository.saveAll(chunk.getItems());
    }
}
