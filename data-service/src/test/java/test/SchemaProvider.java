package test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SchemaProvider implements Serializable {

    private String dialect;
    private Map<String, TableSchema> schemaMap;
    private Map<String, String> joinExpressions;
    private Map<String, String> queryAliases;


    public SchemaProvider() {
    }

    public SchemaProvider(Map<String, TableSchema> schemaMap) {
        this.schemaMap = schemaMap;
    }

    public void addTableSchema(TableSchema tableSchema) {
        if (this.schemaMap == null) {
            this.schemaMap = new HashMap<>();
        }
        this.schemaMap.put(tableSchema.getAlias(), tableSchema);
    }

    public void addQueryAliases(String queryAlias, String tableAlias) {
        if (this.queryAliases == null) {
            this.queryAliases = new HashMap<>();
        }
        this.queryAliases.put(queryAlias, tableAlias);
    }

    public String  getQueryAliases(String queryAlias) {
        return queryAliases.get(queryAlias);
    }

    public void addJoinExpression(String key, String joinExpression) {
        if (this.joinExpressions == null) {
            this.joinExpressions = new HashMap<>();
        }
        this.joinExpressions.put(key, joinExpression);
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getDialect() {
        return dialect;
    }

    public String getJoinExpression(String key) {
        return this.joinExpressions.get(key);
    }

    public TableSchema getTableSchema(String tableName) {
        return this.schemaMap.get(tableName);
    }

    public static class TableSchema {

        private String tableName;
        private String alias;
        private Map<String, String> columns;

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public Map<String, String> getColumns() {
            return columns;
        }

        public String getColumnName(String columnName) {
            return this.columns.get(columnName);
        }

        public void setColumns(Map<String, String> columns) {
            this.columns = columns;
        }

        public void addColumn(String columnAlias, String columnName) {
            if (this.columns == null) {
                columns = new HashMap<>();
            }
            columns.put(columnAlias, columnName);
        }

    }
}
