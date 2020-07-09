package com.bigdata.core.op;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SqlOperatorParser implements OperatorParser {
    @Override
    public SqlExpression parse(Operator operator) {

        if (operator instanceof Operator.And) {
            Operator.And and = (Operator.And) operator;
            return and.getOperators().stream().map(this::parse).reduce((e1, e2) -> combine(e1, e2, "AND")).get();
        } else if (operator instanceof Operator.Or) {
            Operator.Or or = (Operator.Or) operator;
            return or.getOperators().stream().map(this::parse).reduce((e1, e2) -> combine(e1, e2, "OR")).get();
        } else if (operator instanceof Operator.Equals) {
            Operator.Equals equals = (Operator.Equals) operator;
            return this.createExpression(equals.getKey(), " = ?", equals.value);
        } else if (operator instanceof Operator.NotEquals) {
            Operator.NotEquals notEquals = (Operator.NotEquals) operator;
            return this.createExpression(notEquals.getKey(), " <> ?", notEquals.value);
        } else if (operator instanceof Operator.Gt) {
            Operator.Gt gt = (Operator.Gt) operator;
            return this.createExpression(gt.getKey(), " > ?", gt.value);
        } else if (operator instanceof Operator.GtEq) {
            Operator.GtEq gtEq = (Operator.GtEq) operator;
            return this.createExpression(gtEq.getKey(), " >= ?", gtEq.value);
        } else if (operator instanceof Operator.Lt) {
            Operator.Lt lt = (Operator.Lt) operator;
            return this.createExpression(lt.getKey(), " < ?", lt.value);
        } else if (operator instanceof Operator.GtEq) {
            Operator.GtEq gtEq = (Operator.GtEq) operator;
            return this.createExpression(gtEq.getKey(), " >= ?", gtEq.value);
        } else if (operator instanceof Operator.In) {
            Operator.In in = (Operator.In) operator;
            return this.parseMultivalueOperator(in.getKey(), " IN ", in.getValue());
        } else if (operator instanceof Operator.NotIn) {
            Operator.NotIn notIn = (Operator.NotIn) operator;
            return this.parseMultivalueOperator(notIn.getKey(), " NOT IN ", notIn.getValue());
        } else {
            throw new IllegalStateException("Unsupported operator - " + operator.getClass());
        }
    }

    private SqlExpression parseMultivalueOperator(String key, String prefix, Operator.ArrayValue value) {
        List<Operator.SingleValue> values = value.getValue();
        String inClause = Collections.nCopies(values.size(), "?").stream().reduce((v1, v2) -> v1 + "," + v2).get();
        return new SqlExpression(key + prefix + " (" + inClause + ")", value);
    }

    private SqlExpression combine(SqlExpression e1, SqlExpression e2, String joiner) {
        String expr = "(" + e1.getExpression() + ") " + joiner + " (" + e2.getExpression() + ")";
        List<Operator.Value> parameters = flatternParameters(e1.getParameters());
        parameters.addAll(flatternParameters(e2.getParameters()));
        return new SqlExpression(expr, parameters);
    }

    @NotNull
    private List<Operator.Value> flatternParameters(List<Operator.Value> parameters) {
        return parameters.stream().flatMap(value -> {
            if (value instanceof Operator.ArrayValue) {
                return ((Operator.ArrayValue) value).getValue().stream();
            } else {
                return Stream.of(value);
            }
        }).collect(Collectors.toList());
    }

    private SqlExpression createExpression(String key, String operator, Operator.Value value) {
        return new SqlExpression(key + operator, value);
    }
}
