package com.banco.batch.config;

import com.banco.batch.model.ProcessedTransaction;
import com.banco.batch.model.TransactionRecord;
import com.banco.batch.processor.DailyTransactionProcessor;
import com.banco.batch.reader.TransactionFlatFileReader;
import com.banco.batch.writer.ProcessedTransactionWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Job 1: Reporte de Transacciones Diarias.
 */
@Configuration
@RequiredArgsConstructor
public class JobDailyTransactionsConfig {

    private final TransactionFlatFileReader readerFactory;
    private final DailyTransactionProcessor processor;
    private final ProcessedTransactionWriter writer;
    private final LegacyCsvProperties legacyProps;

    @Bean
    @StepScope
    public FlatFileItemReader<TransactionRecord> transactionsReader(
            @Value("#{jobParameters['run.date']}") String date) {
        String fileName = (date != null && !date.isBlank())
                ? "transactions_" + date + ".csv"
                : legacyProps.getFiles().getDailyTransactionsFile();
        return readerFactory.build(fileName);
    }

    @Bean
    public Step dailyTransactionsStep(JobRepository jobRepository,
                                      PlatformTransactionManager txManager,
                                      FlatFileItemReader<TransactionRecord> transactionsReader) {
        return new StepBuilder("dailyTransactionsStep", jobRepository)
                .<TransactionRecord, ProcessedTransaction>chunk(100, txManager)
                .reader(transactionsReader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(50)
                .build();
    }

    @Bean
    public Job dailyTransactionsReportJob(JobRepository jobRepository,
                                          @Qualifier("dailyTransactionsStep") Step dailyTransactionsStep) {
        return new JobBuilder("dailyTransactionsReportJob", jobRepository)
                .start(dailyTransactionsStep)
                .build();
    }
}
