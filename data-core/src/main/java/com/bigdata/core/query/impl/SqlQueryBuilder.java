package com.bigdata.core.query.impl;


import com.bigdata.core.op.LookupExpression;
import com.bigdata.core.op.Operator;
import com.bigdata.core.op.OperatorParser;
import com.bigdata.core.op.SqlOperatorParser;
import com.bigdata.core.query.Catalog;
import com.bigdata.core.query.dialect.Dialect;
import com.bigdata.core.query.dialect.DialectFactory;
import com.bigdata.dao.intf.DataQuery;
import com.bigdata.core.query.QueryBuilder;
import com.bigdata.core.query.TableSchema;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SqlQueryBuilder implements QueryBuilder {

    private Catalog catalog;
    private SqlOperatorParser operatorParser;
    private Dialect dialect;

    public SqlQueryBuilder(Catalog catalog) {
        this.catalog = catalog;
        this.operatorParser = new SqlOperatorParser();
        this.dialect = DialectFactory.getDialect(catalog.getDialect());
    }

    @Override
    public DataQuery buildQuery(LookupExpression expression) {

        Set<TableSchema> selectionSet = expression.getProjections().keySet();
        StringBuilder query = new StringBuilder("SELECT ");
        query.append(this.getProjectionString(expression)).append(" FROM ").append(this.buildSelectionSet(selectionSet));

        Operator.And operator = new Operator.And(expression.getFilters().values());
        OperatorParser.SqlExpression sqlExpression = operatorParser.parse(operator);
        query.append(" WHERE ").append(sqlExpression.getExpression());

        String sqlText = this.addPagination(query.toString(), expression);
        return new DataQuery(sqlText, this.getParameters(sqlExpression));
    }

    private String addPagination(String query, LookupExpression expression) {
        String paginationExpression = this.dialect.getPaginationString(expression.getPageSize(), expression.getPageNumber());
        if (paginationExpression != null) {
            StringBuilder sqlText = new StringBuilder(query).append(" ").append(paginationExpression);
            return sqlText.toString();
        } else {
            return query;
        }
    }

    @NotNull
    private List<Object> getParameters(OperatorParser.SqlExpression sqlExpression) {
        return sqlExpression.getParameters().stream().flatMap(parameter -> {
            if (parameter instanceof Operator.ArrayValue) {
                Operator.ArrayValue arrayValue = (Operator.ArrayValue) parameter;
                return arrayValue.getValue().stream().map(p -> p.getValue());
            } else {
                return Stream.of(parameter.getValue());
            }

        }).collect(Collectors.toList());
    }

    @NotNull
    private String getProjectionString(LookupExpression expression) {
        return expression.getProjections().entrySet().stream().map(pair -> this.getQuotedString(pair.getValue(), pair.getKey())
        ).reduce((v1, v2) -> v1 + ", " + v2).get();
    }

    @NotNull
    private String getQuotedString(Collection<String> columns, TableSchema table) {
        Function<String, String> mapper = columnName -> table.getAlias() + "." + columnName + " as " + this.dialect.getQuotedString(table.getColumnAlias(columnName));
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
