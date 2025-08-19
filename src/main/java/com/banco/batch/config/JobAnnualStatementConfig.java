package com.banco.batch.config;

import com.banco.batch.model.Account;
import com.banco.batch.model.AnnualStatement;
import com.banco.batch.processor.AnnualStatementProcessor;
import com.banco.batch.repository.AccountRepository;
import com.banco.batch.writer.AnnualStatementWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class JobAnnualStatementConfig {

    private final AccountRepository accountRepository;
    private final AnnualStatementProcessor processor;
    private final AnnualStatementWriter writer;

    @Bean
    @StepScope
    public ListItemReader<Account> accountReaderForAnnual() {
        return new ListItemReader<>(accountRepository.findAll());
    }

    @Bean
    public Step annualStatementStep(JobRepository jobRepository,
                                    PlatformTransactionManager txManager,
                                    @Qualifier("accountReaderForAnnual") ListItemReader<Account> accountReaderForAnnual) {
        return new StepBuilder("annualStatementStep", jobRepository)
                .<Account, AnnualStatement>chunk(50, txManager)
                .reader(accountReaderForAnnual)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job annualStatementJob(JobRepository jobRepository,
                                  @Qualifier("annualStatementStep") Step annualStatementStep) {
        return new JobBuilder("annualStatementJob", jobRepository)
                .start(annualStatementStep)
                .build();
    }
}
