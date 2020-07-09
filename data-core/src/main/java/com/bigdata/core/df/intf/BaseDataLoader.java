package com.bigdata.core.df.intf;

import com.bigdata.core.op.LookupExpression;
import com.bigdata.core.op.Operator;
import com.bigdata.core.query.Catalog;
import com.bigdata.core.query.QueryBuilder;
import com.bigdata.core.query.TableSchema;
import com.bigdata.dao.intf.DataDAO;
import com.bigdata.dao.model.Query;
import graphql.language.*;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseDataLoader<T> implements DataFetcher<T> {
    private QueryBuilder queryBuilder;
    private Catalog catalog;
    protected DataDAO dataDAO;

    @Override
    public T get(DataFetchingEnvironment environment) {
        LookupExpression.LookupExpressionBuilder projectionExpression = this.scanProjections(environment.getFields());
        LookupExpression.LookupExpressionBuilder filterExpressionBuilder = this.scanExpressions(environment.getFields());
        LookupExpression expression = projectionExpression.merge(filterExpressionBuilder).build();

        Query query = queryBuilder.buildQuery(expression);
        return this.fetch(query);
    }

    public abstract T fetch(Query query);

    private LookupExpression.LookupExpressionBuilder scanProjections(List<Field> fields) {
        Field field = fields.get(0);
        List<Selection> selections = field.getSelectionSet().getSelections();
        String tableAlias = this.catalog.getTableAlias(field.getName());
        TableSchema tableSchema = this.catalog.getTableSchemaByAlias(tableAlias);

        LookupExpression.LookupExpressionBuilder expressionBuilder = selections.stream().map(s -> parseProjections(s, tableSchema)).reduce((e1, e2) -> e1.merge(e2)).get();

        return expressionBuilder;
    }

    private LookupExpression.LookupExpressionBuilder scanExpressions(List<Field> fields) {
        Field field = fields.get(0);
        String tableAlias = this.catalog.getTableAlias(field.getName());
        TableSchema tableSchema = this.catalog.getTableSchemaByAlias(tableAlias);

        List<Selection> selections = field.getSelectionSet().getSelections();

        LookupExpression.LookupExpressionBuilder expressionBuilder = selections.stream().map(s -> parseFilters(s, tableSchema)).reduce((e1, e2) -> e1.merge(e2)).get();
        field.getArguments().forEach(arg -> expressionBuilder.withfilter(tableSchema, createOperator(arg, tableSchema)));
        return expressionBuilder;
    }

    private LookupExpression.LookupExpressionBuilder parseProjections(Selection selection,
                                                                      TableSchema sourceSchema
    ) {
        Field sfield = (Field) selection;
        SelectionSet childSelections = sfield.getSelectionSet();
        if (childSelections != null) {
            String alias = this.catalog.getTableAlias(sfield.getName());
            TableSchema childSchema = this.catalog.getTableSchemaByAlias(alias);
            return childSelections.getSelections().stream().map(s -> parseProjections(s, childSchema)).reduce((e1, e2) -> e1.merge(e2)).get();
        } else {
            LookupExpression.LookupExpressionBuilder expressionBuilder = new LookupExpression.LookupExpressionBuilder();
            return expressionBuilder.withProjection(sourceSchema, sourceSchema.getColumnName(sfield.getName()));
        }
    }

    private LookupExpression.LookupExpressionBuilder parseFilters(Selection selection,
                                                                  TableSchema tableSchema
    ) {
        Field sfield = (Field) selection;
        SelectionSet childSelections = sfield.getSelectionSet();
        if (childSelections != null) {
            String alias = this.catalog.getTableAlias(sfield.getName());
            TableSchema childSchema = this.catalog.getTableSchemaByAlias(alias);
            return childSelections.getSelections().stream().map(s -> parseFilters(s, childSchema)).reduce((e1, e2) -> e1.merge(e2)).get();
        } else {
            LookupExpression.LookupExpressionBuilder expressionBuilder = new LookupExpression.LookupExpressionBuilder();
            sfield.getArguments().forEach(argument -> {
                expressionBuilder.withfilter(tableSchema, createOperator(argument, tableSchema));
            });
            return expressionBuilder;
        }
    }

    private Operator createOperator(Argument argument, TableSchema tableSchema) {
        String key = tableSchema.getAlias() + "." + tableSchema.getColumnName(argument.getName());
        Value value = argument.getValue();
        if ((value instanceof StringValue)
                | (value instanceof IntValue)
                | (value instanceof BooleanValue)
                | (value instanceof FloatValue)
                | (value instanceof EnumValue)) {
            return new Operator.Equals(key, createValue(value));
        } else if (value instanceof ArrayValue) {
            Operator.ArrayValue arrayValue = (Operator.ArrayValue) this.createValue(value);
            return new Operator.In(key, arrayValue);
        } else {
            throw new IllegalStateException("");
        }
    }

    private Operator.Value createValue(Value value) {
        if (value instanceof StringValue) {
            StringValue stringValue = (StringValue) value;
            return new Operator.StringValue(stringValue.getValue());
        } else if (value instanceof IntValue) {
            IntValue intValue = (IntValue) value;
            return new Operator.NumberValue(intValue.getValue());
        } else if (value instanceof BooleanValue) {
            BooleanValue booleanValue = (BooleanValue) value;
            return new Operator.BooleanValue(booleanValue.isValue());
        } else if (value instanceof FloatValue) {
            FloatValue floatValue = (FloatValue) value;
            return new Operator.NumberValue(floatValue.getValue());
        } else if (value instanceof EnumValue) {
            EnumValue enumValue = (EnumValue) value;
            return new Operator.StringValue(enumValue.getName());
        } else if (value instanceof ArrayValue) {
            ArrayValue arrayValue = (ArrayValue) value;
            List<Operator.SingleValue> values = arrayValue.getValues().stream().map(v -> (Operator.SingleValue) createValue(v)).collect(Collectors.toList());
            return new Operator.ArrayValue(values);
        } else {
            throw new IllegalStateException("");
        }
    }

    public void init(QueryBuilder queryBuilder, Catalog catalog, DataDAO dataDAO) {
        this.queryBuilder = queryBuilder;
        this.catalog = catalog;
        this.dataDAO = dataDAO;
    }
}
