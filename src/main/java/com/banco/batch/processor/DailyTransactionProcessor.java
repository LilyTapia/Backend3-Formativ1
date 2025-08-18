package com.banco.batch.processor;

import com.banco.batch.model.Account;
import com.banco.batch.model.ProcessedTransaction;
import com.banco.batch.model.TransactionRecord;
import com.banco.batch.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Valida cada fila del CSV y marca anomalías con mensaje.
 * Implementa reglas de validación comprehensivas para asegurar consistencia de datos.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyTransactionProcessor implements ItemProcessor<TransactionRecord, ProcessedTransaction> {

    private final AccountRepository accountRepository;

    // Tipos válidos de transacciones del sistema legacy
    private static final List<String> VALID_TIPOS = Arrays.asList(
        "credito", "debito", "transferencia", "pago", "retiro", "deposito"
    );

    // Límites de transacciones
    private static final BigDecimal MAX_TRANSACTION_AMOUNT = new BigDecimal("1000000.00");
    private static final BigDecimal MIN_TRANSACTION_AMOUNT = new BigDecimal("-1000000.00");

    @Override
    public ProcessedTransaction process(TransactionRecord item) {
        log.debug("Processing transaction for account: {}", item.getAccountNumber());

        boolean anomaly = false;
        StringBuilder msg = new StringBuilder();

        // Validación 1: Campos obligatorios
        if (isNullOrEmpty(item.getAccountNumber())) {
            anomaly = true;
            msg.append("Número de cuenta vacío; ");
        }

        if (isNullOrEmpty(item.getAmount())) {
            anomaly = true;
            msg.append("Monto vacío; ");
        }

        if (isNullOrEmpty(item.getTxnDate())) {
            anomaly = true;
            msg.append("Fecha vacía; ");
        }

        // Validación 2: Existencia de cuenta
        Optional<Account> accountOpt = Optional.empty();
        if (!isNullOrEmpty(item.getAccountNumber())) {
            accountOpt = accountRepository.findByAccountNumber(item.getAccountNumber().trim());
            if (accountOpt.isEmpty()) {
                anomaly = true;
                msg.append("Cuenta inexistente; ");
            }
        }

        // Validación 3: Formato y rango de monto
        Double amount = 0d;
        if (!isNullOrEmpty(item.getAmount())) {
            try {
                amount = Double.parseDouble(item.getAmount().trim());
                BigDecimal amountBD = new BigDecimal(amount.toString());

                if (amountBD.compareTo(MAX_TRANSACTION_AMOUNT) > 0) {
                    anomaly = true;
                    msg.append("Monto excede límite máximo; ");
                } else if (amountBD.compareTo(MIN_TRANSACTION_AMOUNT) < 0) {
                    anomaly = true;
                    msg.append("Monto excede límite mínimo; ");
                }

                if (amount == 0) {
                    anomaly = true;
                    msg.append("Monto no puede ser cero; ");
                }

            } catch (NumberFormatException e) {
                anomaly = true;
                msg.append("Formato de monto inválido; ");
                log.warn("Invalid amount format: {} for account: {}", item.getAmount(), item.getAccountNumber());
            }
        }

        // Validación 4: Formato de fecha
        Date date = null;
        if (!isNullOrEmpty(item.getTxnDate())) {
            try {
                LocalDate localDate = LocalDate.parse(item.getTxnDate().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
                date = Date.valueOf(localDate);

                // Validar que la fecha no sea futura
                if (localDate.isAfter(LocalDate.now())) {
                    anomaly = true;
                    msg.append("Fecha futura no permitida; ");
                }

                // Validar que la fecha no sea muy antigua (más de 10 años)
                if (localDate.isBefore(LocalDate.now().minusYears(10))) {
                    anomaly = true;
                    msg.append("Fecha muy antigua; ");
                }

            } catch (DateTimeParseException e) {
                anomaly = true;
                msg.append("Formato de fecha inválido (usar YYYY-MM-DD); ");
                date = new Date(System.currentTimeMillis());
                log.warn("Invalid date format: {} for account: {}", item.getTxnDate(), item.getAccountNumber());
            }
        } else {
            date = new Date(System.currentTimeMillis());
        }

        // Validación 5: Tipo de transacción (legacy format)
        String tipo = item.getCategory(); // getCategory() mapea a getTipo()
        if (!isNullOrEmpty(tipo)) {
            tipo = tipo.trim().toLowerCase();
            if (!VALID_TIPOS.contains(tipo)) {
                anomaly = true;
                msg.append("Tipo de transacción inválido; ");
            }
        } else {
            tipo = "unknown";
            anomaly = true;
            msg.append("Tipo de transacción vacío; ");
        }

        // Validación 6: Reglas de negocio específicas por tipo de cuenta
        if (accountOpt.isPresent() && amount != null) {
            Account account = accountOpt.get();

            // Para cuentas de ahorro, validar retiros excesivos
            if ("SAVINGS".equals(account.getType()) && amount < 0) {
                double newBalance = account.getBalance() + amount;
                if (newBalance < 0) {
                    anomaly = true;
                    msg.append("Retiro excede saldo disponible; ");
                }
            }

            // Para préstamos, validar que los pagos sean positivos
            if ("LOAN".equals(account.getType()) && "pago".equals(tipo) && amount < 0) {
                anomaly = true;
                msg.append("Pago de préstamo debe ser positivo; ");
            }
        }

        // Log de anomalías para monitoreo
        if (anomaly) {
            log.warn("Transaction anomaly detected for account {}: {}",
                item.getAccountNumber(), msg.toString().trim());
        }

        return ProcessedTransaction.builder()
                .accountNumber(item.getAccountNumber() != null ? item.getAccountNumber().trim() : null)
                .txnDate(date)
                .amount(amount)
                .category(tipo)
                .anomaly(anomaly)
                .message(msg.length() == 0 ? null : msg.toString().trim())
                .build();
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
