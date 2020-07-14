package com.bigdata.core.query;

import java.util.HashMap;
import java.util.Map;

public class Catalog {

    private String dialect;
    private Map<String, TableSchema> schemaByAlias;
    private Map<String, String> joinExpressions;
    private Map<String, String> queryAliases;
    private Map<String, String> dataloaders;

    private Catalog(String dialect, Map<String, TableSchema> schemaByAlias, Map<String, String> joinExpressions, Map<String, String> queryAliases, Map<String, String> dataloaders) {
        this.dialect = dialect;
        this.schemaByAlias = schemaByAlias;
        this.joinExpressions = joinExpressions;
        this.queryAliases = queryAliases;
        this.dataloaders = dataloaders;
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

    public Map<String, String> getDataloaders() {
        return dataloaders;
    }

    public String getJoinExpression(String key) {
        return this.joinExpressions.get(key);
    }

    static class CatalogBuilder {
        private String dialect;
        private Map<String, TableSchema> schemaByAlias;
        private Map<String, String> joinExpressions;
        private Map<String, String> queryAliases;
        private Map<String, String> dataloaders;

        CatalogBuilder() {
            this.schemaByAlias = new HashMap<>(2);
            this.joinExpressions = new HashMap<>(2);
            this.queryAliases = new HashMap<>(2);
            this.dataloaders = new HashMap<>(2);
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

        public CatalogBuilder withDataLoader(String queryAlias, String dataLoader) {
            this.dataloaders.put(queryAlias, dataLoader);
            return this;
        }

        public Catalog build() {
            return new Catalog(dialect, schemaByAlias, joinExpressions, queryAliases, dataloaders);
        }
    }
}
