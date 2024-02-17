package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class UploadedFileSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("file_id", table, columnPrefix + "_file_id"));
        columns.add(Column.aliased("filename", table, columnPrefix + "_filename"));
        columns.add(Column.aliased("data", table, columnPrefix + "_data"));
        columns.add(Column.aliased("data_content_type", table, columnPrefix + "_data_content_type"));

        return columns;
    }
}
