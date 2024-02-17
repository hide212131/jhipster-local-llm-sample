package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.UploadedFile;
import io.r2dbc.spi.Row;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link UploadedFile}, with proper type conversions.
 */
@Service
public class UploadedFileRowMapper implements BiFunction<Row, String, UploadedFile> {

    private final ColumnConverter converter;

    public UploadedFileRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link UploadedFile} stored in the database.
     */
    @Override
    public UploadedFile apply(Row row, String prefix) {
        UploadedFile entity = new UploadedFile();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFileId(converter.fromRow(row, prefix + "_file_id", UUID.class));
        entity.setFilename(converter.fromRow(row, prefix + "_filename", String.class));
        entity.setDataContentType(converter.fromRow(row, prefix + "_data_content_type", String.class));
        entity.setData(converter.fromRow(row, prefix + "_data", byte[].class));
        return entity;
    }
}
