package com.bigdata.core.query;

import java.util.HashMap;
import java.util.Map;

public class Catalog {

    private String dialect;
    private Map<String, TableSchema> schemaByAlias;
    private Map<String, String> joinExpressions;
    private Map<String, String> queryAliases;

    private Catalog(String dialect, Map<String, TableSchema> schemaByAlias, Map<String, String> joinExpressions, Map<String, String> queryAliases) {
        this.dialect = dialect;
        this.schemaByAlias = schemaByAlias;
        this.joinExpressions = joinExpressions;
        this.queryAliases = queryAliases;
    }

    public String getTableAlias(String queryAlias) {
        return queryAliases.get(queryAlias);
    }

    public TableSchema getTableSchemaByAlias(String tableAlias) {
        return schemaByAlias.get(tableAlias);
    }

    public String getDialect() {
        return this.dialect;
    }

    public String getJoinExpression(String key) {
        return this.joinExpressions.get(key);
    }

    static class CatalogBuilder {
        private String dialect;
        private Map<String, TableSchema> schemaByAlias;
        private Map<String, String> joinExpressions;
        private Map<String, String> queryAliases;

        CatalogBuilder() {
            this.schemaByAlias = new HashMap<>(2);
            this.joinExpressions = new HashMap<>(2);
            this.queryAliases = new HashMap<>(2);
        }


        public CatalogBuilder withDialect(String dialect) {
            this.dialect = dialect;
            return this;
        }

        public CatalogBuilder withTableSchema(TableSchema tableSchema) {
            this.schemaByAlias.put(tableSchema.getAlias(), tableSchema);
            return this;
        }

        public CatalogBuilder withJoinExpressions(String key, String expression) {
            this.joinExpressions.put(key, expression);
            return this;
        }

        public CatalogBuilder withQueryAliases(String queryAlias, String tableAlias) {
            this.queryAliases.put(queryAlias, tableAlias);
            return this;
        }

        public Catalog build() {
            return new Catalog(dialect, schemaByAlias, joinExpressions, queryAliases);
        }
    }
}
