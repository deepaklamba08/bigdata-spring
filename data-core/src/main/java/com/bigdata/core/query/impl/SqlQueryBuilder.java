package com.bigdata.core.query.impl;


import com.bigdata.core.op.LookupExpression;
import com.bigdata.core.query.*;
import graphql.language.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.bigdata.core.op.Operator;
import com.bigdata.core.op.OperatorParser;
import com.bigdata.core.op.SqlOperatorParser;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SqlQueryBuilder implements QueryBuilder {

    private Catalog catalog;
    //    private SchemaProvider schemaProvider;
    private SqlOperatorParser operatorParser;

    public SqlQueryBuilder(Catalog catalog/*SchemaProvider schemaProvider*/) {
        //      this.schemaProvider = schemaProvider;
        this.catalog = catalog;
        this.operatorParser = new SqlOperatorParser();
    }

    @Override
    public DataQuery buildQuery(LookupExpression expression) {

        Optional<String> projections = expression.getProjections().entrySet().stream().map(pair ->
                this.getQuotedString(pair.getValue(), pair.getKey(), catalog.getDialect())
        ).reduce((v1, v2) -> v1 + ", " + v2);
        StringBuilder query = new StringBuilder("SELECT ");
        Set<TableSchema> selectionSet = expression.getProjections().keySet();
        query.append(projections.get()).append(" FROM ").append(this.buildSelectionSet(selectionSet));

        Operator.And operator = new Operator.And(expression.getFilters().values());
        OperatorParser.SqlExpression sqlExpression = operatorParser.parse(operator);
        query.append(" WHERE ").append(sqlExpression.getExpression());
        List<Object> parameters = sqlExpression.getParameters().stream().flatMap(parameter -> {
            if (parameter instanceof Operator.ArrayValue) {
                Operator.ArrayValue arrayValue = (Operator.ArrayValue) parameter;
                return arrayValue.getValue().stream().map(p -> p.getValue());
            } else {
                return Stream.of(parameter.getValue());
            }

        }).collect(Collectors.toList());

        return new DataQuery(query.toString(), parameters);
    }

    @NotNull
    private String getQuotedString(Collection<String> columns, TableSchema table, String dialect) {
        Function<String, String> mapper;
        if ("HIVE".equalsIgnoreCase(dialect)) {
            mapper = columnName -> table.getAlias() + "." + columnName + " as " + table.getColumnAlias(columnName);
        } else {
            mapper = columnName -> table.getAlias() + "." + columnName + " as \"" + table.getColumnAlias(columnName) + "\"";
        }
        return columns.stream().map(mapper).reduce((v1, v2) -> v1 + ", " + v2).get();
    }

    private String buildSelectionSet(Set<TableSchema> tableNames) {
        Iterator<TableSchema> tables = tableNames.iterator();
        if (tableNames.size() == 1) {
            TableSchema tableSchema = tables.next();
            return tableSchema.getTableName() + " " + tableSchema.getAlias();
        } else {
            String key = tableNames.stream().map(t -> t.getAlias()).sorted(String::compareTo).reduce((a1, a2) -> a1 + "~" + a2).get();
            return this.catalog.getJoinExpression(key);
        }
    }

}
