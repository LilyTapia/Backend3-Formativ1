package com.banco.batch.config;

import com.banco.batch.model.Account;
import com.banco.batch.model.InterestLedger;
import com.banco.batch.processor.MonthlyInterestProcessor;
import com.banco.batch.repository.AccountRepository;
import com.banco.batch.writer.MonthlyInterestWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class JobMonthlyInterestConfig {

    private final AccountRepository accountRepository;
    private final MonthlyInterestProcessor processor;
    private final MonthlyInterestWriter writer;

    @Bean
    @StepScope
    public ListItemReader<Account> accountReaderForMonthly() {
        return new ListItemReader<>(accountRepository.findAll());
    }

    @Bean
    public Step monthlyInterestStep(JobRepository jobRepository,
                                    PlatformTransactionManager txManager,
                                    ListItemReader<Account> accountReaderForMonthly,
                                    @Value("#{jobParameters['run.period']}") String period) {
        processor.setPeriodYyyymm(period);
        return new StepBuilder("monthlyInterestStep", jobRepository)
                .<Account, InterestLedger>chunk(50, txManager)
                .reader(accountReaderForMonthly)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(3)
                .build();
    }

    @Bean
    public Job monthlyInterestJob(JobRepository jobRepository,
                                  Step monthlyInterestStep) {
        return new JobBuilder("monthlyInterestJob", jobRepository)
                .start(monthlyInterestStep)
                .build();
    }
}
