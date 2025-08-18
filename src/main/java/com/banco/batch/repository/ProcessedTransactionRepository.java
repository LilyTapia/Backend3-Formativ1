package com.banco.batch.repository;

import com.banco.batch.model.ProcessedTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;

public interface ProcessedTransactionRepository extends JpaRepository<ProcessedTransaction, Long> {
    List<ProcessedTransaction> findByAccountNumberAndTxnDateBetween(
            String accountNumber, Date start, Date end);
}
