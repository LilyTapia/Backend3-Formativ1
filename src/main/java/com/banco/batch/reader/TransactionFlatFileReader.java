package com.banco.batch.reader;

import com.banco.batch.config.LegacyCsvProperties;
import com.banco.batch.model.TransactionRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Reader que toma los nombres de columnas reales desde application.yml (legacy.columns.*).
 * Permite usar CSVs del sistema legacy sin tocar Java.
 */
@Component
@RequiredArgsConstructor
public class TransactionFlatFileReader {

    private final LegacyCsvProperties props;

    public FlatFileItemReader<TransactionRecord> build(String fileName) {
        // Nombres de columnas del CSV legacy: id, fecha, monto, tipo
        String[] legacyNames = new String[] {"id", "fecha", "monto", "tipo"};

        return new FlatFileItemReaderBuilder<TransactionRecord>()
                .name("transactionReader")
                .resource(new ClassPathResource("data/" + fileName))
                .delimited()
                .names(legacyNames)
                .fieldSetMapper(fieldSet -> {
                    TransactionRecord tr = new TransactionRecord();
                    tr.setId(fieldSet.readString("id"));
                    tr.setFecha(fieldSet.readString("fecha"));
                    tr.setMonto(fieldSet.readString("monto"));
                    tr.setTipo(fieldSet.readString("tipo"));
                    return tr;
                })
                .linesToSkip(1)
                .strict(false)
                .build();
    }
}
