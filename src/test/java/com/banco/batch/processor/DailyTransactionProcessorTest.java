package com.banco.batch.processor;

import com.banco.batch.model.Account;
import com.banco.batch.model.ProcessedTransaction;
import com.banco.batch.model.TransactionRecord;
import com.banco.batch.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyTransactionProcessorTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private DailyTransactionProcessor processor;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .id(1L)
                .accountNumber("ACC-1001")
                .type("SAVINGS")
                .balance(150000.0)
                .annualInterestRate(0.03)
                .build();
    }

    @Test
    void process_ValidTransaction_ShouldReturnNoAnomaly() {
        // Given
        TransactionRecord record = TransactionRecord.builder()
                .id("1")
                .fecha("2025-08-01")
                .monto("5000.00")
                .tipo("credito")
                .build();

        when(accountRepository.findByAccountNumber("1"))
                .thenReturn(Optional.of(testAccount));

        // When
        ProcessedTransaction result = processor.process(record);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAnomaly()).isFalse();
        assertThat(result.getMessage()).isNull();
        assertThat(result.getAccountNumber()).isEqualTo("1");
        assertThat(result.getAmount()).isEqualTo(5000.0);
        assertThat(result.getCategory()).isEqualTo("credito");
    }

    @Test
    void process_NonExistentAccount_ShouldReturnAnomaly() {
        // Given
        TransactionRecord record = TransactionRecord.builder()
                .id("9999")
                .fecha("2025-08-01")
                .monto("5000.00")
                .tipo("credito")
                .build();

        when(accountRepository.findByAccountNumber("9999"))
                .thenReturn(Optional.empty());

        // When
        ProcessedTransaction result = processor.process(record);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAnomaly()).isTrue();
        assertThat(result.getMessage()).contains("Cuenta inexistente");
    }

    @Test
    void process_InvalidAmount_ShouldReturnAnomaly() {
        // Given
        TransactionRecord record = TransactionRecord.builder()
                .id("1")
                .fecha("2025-08-01")
                .monto("abc")
                .tipo("credito")
                .build();

        when(accountRepository.findByAccountNumber("1"))
                .thenReturn(Optional.of(testAccount));

        // When
        ProcessedTransaction result = processor.process(record);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAnomaly()).isTrue();
        assertThat(result.getMessage()).contains("Formato de monto inválido");
    }

    @Test
    void process_InvalidDate_ShouldReturnAnomaly() {
        // Given
        TransactionRecord record = TransactionRecord.builder()
                .id("1")
                .fecha("invalid-date")
                .monto("5000.00")
                .tipo("credito")
                .build();

        when(accountRepository.findByAccountNumber("1"))
                .thenReturn(Optional.of(testAccount));

        // When
        ProcessedTransaction result = processor.process(record);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAnomaly()).isTrue();
        assertThat(result.getMessage()).contains("Formato de fecha inválido");
    }

    @Test
    void process_ZeroAmount_ShouldReturnAnomaly() {
        // Given
        TransactionRecord record = TransactionRecord.builder()
                .id("1")
                .fecha("2025-08-01")
                .monto("0")
                .tipo("credito")
                .build();

        when(accountRepository.findByAccountNumber("1"))
                .thenReturn(Optional.of(testAccount));

        // When
        ProcessedTransaction result = processor.process(record);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAnomaly()).isTrue();
        assertThat(result.getMessage()).contains("Monto no puede ser cero");
    }

    @Test
    void process_ExcessiveWithdrawal_ShouldReturnAnomaly() {
        // Given
        TransactionRecord record = TransactionRecord.builder()
                .id("1")
                .fecha("2025-08-01")
                .monto("-200000.00")
                .tipo("debito")
                .build();

        when(accountRepository.findByAccountNumber("1"))
                .thenReturn(Optional.of(testAccount));

        // When
        ProcessedTransaction result = processor.process(record);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAnomaly()).isTrue();
        assertThat(result.getMessage()).contains("Retiro excede saldo disponible");
    }

    @Test
    void process_InvalidCategory_ShouldReturnAnomaly() {
        // Given
        TransactionRecord record = TransactionRecord.builder()
                .id("1")
                .fecha("2025-08-01")
                .monto("5000.00")
                .tipo("invalid_tipo")
                .build();

        when(accountRepository.findByAccountNumber("1"))
                .thenReturn(Optional.of(testAccount));

        // When
        ProcessedTransaction result = processor.process(record);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAnomaly()).isTrue();
        assertThat(result.getMessage()).contains("Tipo de transacción inválido");
    }

    @Test
    void process_FutureDate_ShouldReturnAnomaly() {
        // Given
        TransactionRecord record = TransactionRecord.builder()
                .id("1")
                .fecha("2030-12-31")
                .monto("5000.00")
                .tipo("credito")
                .build();

        when(accountRepository.findByAccountNumber("1"))
                .thenReturn(Optional.of(testAccount));

        // When
        ProcessedTransaction result = processor.process(record);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAnomaly()).isTrue();
        assertThat(result.getMessage()).contains("Fecha futura no permitida");
    }

    @Test
    void process_EmptyFields_ShouldReturnAnomaly() {
        // Given
        TransactionRecord record = TransactionRecord.builder()
                .id("")
                .fecha("")
                .monto("")
                .tipo("")
                .build();

        // When
        ProcessedTransaction result = processor.process(record);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAnomaly()).isTrue();
        assertThat(result.getMessage()).contains("Número de cuenta vacío");
        assertThat(result.getMessage()).contains("Monto vacío");
        assertThat(result.getMessage()).contains("Fecha vacía");
        assertThat(result.getMessage()).contains("Tipo de transacción vacío");
    }

    @Test
    void process_ExcessiveAmount_ShouldReturnAnomaly() {
        // Given
        TransactionRecord record = TransactionRecord.builder()
                .id("1")
                .fecha("2025-08-01")
                .monto("1500000.00")
                .tipo("credito")
                .build();

        when(accountRepository.findByAccountNumber("1"))
                .thenReturn(Optional.of(testAccount));

        // When
        ProcessedTransaction result = processor.process(record);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAnomaly()).isTrue();
        assertThat(result.getMessage()).contains("Monto excede límite máximo");
    }

    @Test
    void process_LoanAccountNegativePayment_ShouldReturnAnomaly() {
        // Given
        Account loanAccount = Account.builder()
                .id(2L)
                .accountNumber("2")
                .type("LOAN")
                .balance(-500000.0)
                .annualInterestRate(0.12)
                .build();

        TransactionRecord record = TransactionRecord.builder()
                .id("2")
                .fecha("2025-08-01")
                .monto("-5000.00")
                .tipo("pago")
                .build();

        when(accountRepository.findByAccountNumber("2"))
                .thenReturn(Optional.of(loanAccount));

        // When
        ProcessedTransaction result = processor.process(record);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAnomaly()).isTrue();
        assertThat(result.getMessage()).contains("Pago de préstamo debe ser positivo");
    }
}
