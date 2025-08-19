package com.banco.batch.integration;

import com.banco.batch.model.Account;
import com.banco.batch.model.ProcessedTransaction;
import com.banco.batch.repository.AccountRepository;
import com.banco.batch.repository.ProcessedTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.batch.job.enabled=false",
    "legacy.files.daily-transactions-file=transactions_test_errors.csv"
})
class DailyTransactionJobIntegrationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    @Qualifier("dailyTransactionsReportJob")
    private Job dailyTransactionsReportJob;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProcessedTransactionRepository processedTransactionRepository;

    @BeforeEach
    void setUp() {
        jobRepositoryTestUtils.removeJobExecutions();
        processedTransactionRepository.deleteAll();
        
        // Setup test accounts that match the CSV data
        accountRepository.deleteAll();
        Account account101 = Account.builder()
                .accountNumber("101")
                .type("SAVINGS")
                .balance(150000.0)
                .annualInterestRate(0.03)
                .build();

        Account account102 = Account.builder()
                .accountNumber("102")
                .type("SAVINGS")
                .balance(50000.0)
                .annualInterestRate(0.03)
                .build();

        Account account103 = Account.builder()
                .accountNumber("103")
                .type("SAVINGS")
                .balance(25000.0)
                .annualInterestRate(0.03)
                .build();

        Account account201 = Account.builder()
                .accountNumber("201")
                .type("LOAN")
                .balance(-500000.0)
                .annualInterestRate(0.12)
                .build();

        Account account202 = Account.builder()
                .accountNumber("202")
                .type("LOAN")
                .balance(-300000.0)
                .annualInterestRate(0.12)
                .build();

        Account account104 = Account.builder()
                .accountNumber("104")
                .type("SAVINGS")
                .balance(10000.0)
                .annualInterestRate(0.03)
                .build();

        Account account105 = Account.builder()
                .accountNumber("105")
                .type("SAVINGS")
                .balance(5000.0)
                .annualInterestRate(0.03)
                .build();

        Account account106 = Account.builder()
                .accountNumber("106")
                .type("SAVINGS")
                .balance(15000.0)
                .annualInterestRate(0.03)
                .build();

        Account account203 = Account.builder()
                .accountNumber("203")
                .type("LOAN")
                .balance(-100000.0)
                .annualInterestRate(0.12)
                .build();

        Account account204 = Account.builder()
                .accountNumber("204")
                .type("LOAN")
                .balance(-50000.0)
                .annualInterestRate(0.12)
                .build();

        accountRepository.save(account101);
        accountRepository.save(account102);
        accountRepository.save(account103);
        accountRepository.save(account201);
        accountRepository.save(account202);
        accountRepository.save(account104);
        accountRepository.save(account105);
        accountRepository.save(account106);
        accountRepository.save(account203);
        accountRepository.save(account204);
        
        jobLauncherTestUtils.setJob(dailyTransactionsReportJob);
    }

    @Test
    void testDailyTransactionJob_WithErrorData_ShouldProcessAllRecords() throws Exception {
        // Given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("run.date", "")
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // Then
        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");
        
        List<ProcessedTransaction> processedTransactions = processedTransactionRepository.findAll();
        assertThat(processedTransactions).isNotEmpty();
        
        // Verify that anomalies were detected
        long anomalyCount = processedTransactions.stream()
                .mapToLong(t -> t.getAnomaly() ? 1 : 0)
                .sum();
        
        assertThat(anomalyCount).isGreaterThan(0);
        
        // Verify specific anomalies
        boolean hasNonExistentAccountAnomaly = processedTransactions.stream()
                .anyMatch(t -> t.getMessage() != null && t.getMessage().contains("Cuenta inexistente"));
        assertThat(hasNonExistentAccountAnomaly).isTrue();
        
        boolean hasInvalidAmountAnomaly = processedTransactions.stream()
                .anyMatch(t -> t.getMessage() != null && t.getMessage().contains("Formato de monto inválido"));
        assertThat(hasInvalidAmountAnomaly).isTrue();
        
        boolean hasInvalidDateAnomaly = processedTransactions.stream()
                .anyMatch(t -> t.getMessage() != null && t.getMessage().contains("Formato de fecha inválido"));
        assertThat(hasInvalidDateAnomaly).isTrue();
    }

    @Test
    void testDailyTransactionJob_WithValidData_ShouldProcessSuccessfully() throws Exception {
        // Given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("run.date", "2025-08-02")
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // Then
        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");
        
        List<ProcessedTransaction> processedTransactions = processedTransactionRepository.findAll();
        assertThat(processedTransactions).isNotEmpty();
        
        // Verify that most transactions are valid (should have minimal anomalies)
        long validCount = processedTransactions.stream()
                .mapToLong(t -> !t.getAnomaly() ? 1 : 0)
                .sum();
        
        assertThat(validCount).isGreaterThan(0);
    }
}
