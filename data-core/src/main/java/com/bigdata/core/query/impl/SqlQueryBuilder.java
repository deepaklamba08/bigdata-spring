package com.bigdata.core.query.impl;


import com.bigdata.core.query.DataQuery;
import com.bigdata.core.query.QueryBuilder;
import com.bigdata.core.query.SchemaProvider;
import graphql.language.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SqlQueryBuilder implements QueryBuilder {

    private SchemaProvider schemaProvider;

    public SqlQueryBuilder(SchemaProvider schemaProvider) {
        this.schemaProvider = schemaProvider;
    }

    @Override
    public DataQuery buildQuery(Map<String, List<String>> selectionSet, Map<String, List<Pair<String, Value>>> expressions) {
        Optional<String> projections = selectionSet.entrySet().stream().map(pair -> {
            String tableAlias = this.schemaProvider.getQueryAliases(pair.getKey());
            SchemaProvider.TableSchema table = schemaProvider.getTableSchema(tableAlias);
            return this.getQuotedString(pair.getValue(), table, schemaProvider.getDialect());
        }).reduce((v1, v2) -> v1 + ", " + v2);

        StringBuilder query = new StringBuilder("SELECT ");
        query.append(projections.get()).append(" FROM ").append(this.buildSelectionSet(selectionSet.keySet()));
        List<Pair<String, Object>> expressPairs = expressions.entrySet().stream().flatMap(pair -> {
            String tableAlias = this.schemaProvider.getQueryAliases(pair.getKey());
            SchemaProvider.TableSchema table = schemaProvider.getTableSchema(tableAlias);
            Stream<Pair<String, Object>> expressionPairs = pair.getValue().stream().map(expression -> this.parseExpression(table.getColumnName(expression.getFirst()), tableAlias, expression.getSecond()));
            return expressionPairs;
        }).collect(Collectors.toList());

        List<Object> parameters = expressPairs.stream().map(p -> p.getSecond()).collect(Collectors.toList());
        Optional<String> whereClause = expressPairs.stream().map(p -> p.getFirst()).reduce((e1, e2) -> e1 + " AND " + e2);
        if (whereClause.isPresent()) {
            query.append(" WHERE (").append(whereClause.get()).append(")");
        }

        return new DataQuery(query.toString(), parameters);
    }

    @NotNull
    private String getQuotedString(List<String> columnAliases, SchemaProvider.TableSchema table, String dialect) {
        Function<String, String> mapper = null;
        if ("HIVE".equalsIgnoreCase(dialect)) {
            mapper = columnAlias -> table.getAlias() + "." + table.getColumnName(columnAlias) + " as " + columnAlias;
        } else {
            mapper = columnAlias -> table.getAlias() + "." + table.getColumnName(columnAlias) + " as \"" + columnAlias + "\"";
        }
        return columnAliases.stream().map(mapper).reduce((v1, v2) -> v1 + ", " + v2).get();
    }

    private Pair<String, Object> parseExpression(String columnName, String tableAlias, Value value) {

        if (value instanceof BooleanValue) {
            String expression = tableAlias + "." + columnName + " = ? ";
            BooleanValue booleanValue = (BooleanValue) value;
            return Pair.of(expression, booleanValue.isValue());
        } else if (value instanceof EnumValue) {
            String expression = tableAlias + "." + columnName + " = ? ";
            EnumValue enumValue = (EnumValue) value;
            return Pair.of(expression, enumValue.getName());
        } else if (value instanceof FloatValue) {
            String expression = tableAlias + "." + columnName + " = ? ";
            FloatValue floatValue = (FloatValue) value;
            return Pair.of(expression, floatValue.getValue());
        } else if (value instanceof IntValue) {
            String expression = tableAlias + "." + columnName + " = ? ";
            IntValue intValue = (IntValue) value;
            return Pair.of(expression, intValue.getValue());
        } else if (value instanceof StringValue) {
            String expression = tableAlias + "." + columnName + " = ? ";
            StringValue stringValue = (StringValue) value;
            return Pair.of(expression, stringValue.getValue());
        } else {
            throw new IllegalStateException("value type not supported - " + value.getClass());
        }
    }


    private String buildSelectionSet(Set<String> tableAliases) {
        Iterator<String> aliasesItr = tableAliases.iterator();
        if (tableAliases.size() == 1) {
            String tableAlias = this.schemaProvider.getQueryAliases(aliasesItr.next());
            SchemaProvider.TableSchema tableSchema = this.schemaProvider.getTableSchema(tableAlias);
            return tableSchema.getTableName() + " " + tableSchema.getAlias();
        } else {
            String key = tableAliases.stream().sorted(String::compareTo).reduce((a1, a2) -> a1 + "~" + a2).get();
            return this.schemaProvider.getJoinExpression(key);
        }
    }

}
