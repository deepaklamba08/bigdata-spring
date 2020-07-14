package com.bigdata.core.op;

import com.bigdata.core.query.TableSchema;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public class LookupExpression {

    private final Map<TableSchema, Set<String>> projections;
    private final Map<TableSchema, Operator> filters;

    private final int pageSize;
    private final int pageNumber;

    private LookupExpression(Map<TableSchema, Set<String>> projections, Map<TableSchema, Operator> filters, int pageSize, int pageNumber) {
        this.projections = projections;
        this.filters = filters;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }

    public Map<TableSchema, Operator> getFilters() {
        return filters;
    }

    public Map<TableSchema, Set<String>> getProjections() {
        return projections;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public static class LookupExpressionBuilder {
        private Map<TableSchema, Set<String>> projections;
        private Map<TableSchema, Operator> filters;

        private int pageSize;
        private int pageNumber;


        public LookupExpressionBuilder() {
            this.projections = new HashMap<>(2);
            this.filters = new HashMap<>(2);
        }

        public LookupExpressionBuilder withProjection(TableSchema tableSchema, String columnName) {

            BiFunction<TableSchema, Set<String>, Set<String>> biFunction = (tableSchema1, columns) -> {
                if (columns == null) {
                    columns = new HashSet<>();
                }
                columns.add(columnName);
                return columns;
            };
            projections.compute(tableSchema, biFunction);
            return this;
        }


        public LookupExpressionBuilder withfilter(TableSchema tableSchema, Operator operator) {
            BiFunction<TableSchema, Operator, Operator> biFunction = (tableSchema1, filter) ->
                    filter != null ? new Operator.And(filter, operator) : operator;
            filters.compute(tableSchema, biFunction);
            return this;
        }

        public LookupExpressionBuilder withPageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public LookupExpressionBuilder withPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
            return this;
        }

        public LookupExpressionBuilder merge(LookupExpressionBuilder expressionBuilder) {
            expressionBuilder.projections.entrySet().forEach(entry -> {
                BiFunction<TableSchema, Set<String>, Set<String>> biFunction = (tableSchema, column) -> {
                    Set<String> columns = entry.getValue();
                    columns.addAll(column);
                    return columns;
                };
                this.projections.compute(entry.getKey(), biFunction);
            });
            expressionBuilder.filters.entrySet().forEach(entry -> {
                BiFunction<TableSchema, Operator, Operator> biFunction = (tableSchema, operator) ->
                        operator != null ? new Operator.And(entry.getValue(), operator) : entry.getValue();
                this.filters.compute(entry.getKey(), biFunction);
            });

            return this;
        }

        public LookupExpression build() {
            return new LookupExpression(this.projections, this.filters, this.pageSize, this.pageNumber);
        }

    }
}
