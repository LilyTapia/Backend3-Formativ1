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
        // Obtener nombres de columnas desde application.yml (legacy.columns.*)
        LegacyCsvProperties.Columns columns = props.getColumns();
        String[] legacyNames = new String[] {
            columns.getId(),
            columns.getFecha(),
            columns.getMonto(),
            columns.getTipo()
        };

        return new FlatFileItemReaderBuilder<TransactionRecord>()
                .name("transactionReader")
                .resource(new ClassPathResource("data/" + fileName))
                .delimited()
                .names(legacyNames)
                .fieldSetMapper(fieldSet -> {
                    TransactionRecord tr = new TransactionRecord();
                    tr.setId(fieldSet.readString(columns.getId()));
                    tr.setFecha(fieldSet.readString(columns.getFecha()));
                    tr.setMonto(fieldSet.readString(columns.getMonto()));
                    tr.setTipo(fieldSet.readString(columns.getTipo()));
                    return tr;
                })
                .linesToSkip(1)
                .strict(false)
                .build();
    }
}
