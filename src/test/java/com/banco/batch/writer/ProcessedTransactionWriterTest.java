package com.banco.batch.writer;

import com.banco.batch.model.ProcessedTransaction;
import com.banco.batch.repository.ProcessedTransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProcessedTransactionWriterTest {

    @Mock
    private ProcessedTransactionRepository repository;

    @InjectMocks
    private ProcessedTransactionWriter writer;

    @Test
    void write_ShouldSaveAllTransactions() {
        // Given
        ProcessedTransaction tx1 = ProcessedTransaction.builder()
                .accountNumber("ACC-1001")
                .txnDate(Date.valueOf(LocalDate.now()))
                .amount(5000.0)
                .category("DEPOSIT")
                .anomaly(false)
                .message(null)
                .build();

        ProcessedTransaction tx2 = ProcessedTransaction.builder()
                .accountNumber("ACC-2001")
                .txnDate(Date.valueOf(LocalDate.now()))
                .amount(-1000.0)
                .category("WITHDRAWAL")
                .anomaly(true)
                .message("Test anomaly")
                .build();

        List<ProcessedTransaction> transactions = Arrays.asList(tx1, tx2);
        Chunk<ProcessedTransaction> chunk = new Chunk<>(transactions);

        // When
        writer.write(chunk);

        // Then
        verify(repository).saveAll(transactions);
    }
}
