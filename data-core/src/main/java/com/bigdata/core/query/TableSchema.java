package com.bigdata.core.query;

import java.util.Map;
import java.util.stream.Collectors;

public class TableSchema {

    private final String tableName;
    private final String alias;
    private final Map<String, String> nameByAlias;
    private final Map<String, String> aliasByName;

    public TableSchema(String tableName, String alias, Map<String, String> nameByAlias) {
        this.tableName = tableName;
        this.alias = alias;
        this.nameByAlias = nameByAlias;
        this.aliasByName = nameByAlias.entrySet().stream().collect(Collectors.toMap(k -> k.getValue(), v -> v.getKey()));
    }

    public String getAlias() {
        return alias;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName(String columnAlias) {
        return this.nameByAlias.get(columnAlias);
    }

    public String getColumnAlias(String columnName) {
        return this.aliasByName.get(columnName);
    }
}
