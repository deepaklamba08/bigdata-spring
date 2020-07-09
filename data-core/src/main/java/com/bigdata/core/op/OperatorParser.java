package com.bigdata.core.op;

import java.util.Arrays;
import java.util.List;

public interface OperatorParser {

    public interface Expression {

    }

    public class SqlExpression implements Expression {
        private final String expression;
        private final List<Operator.Value> parameters;

        public SqlExpression(String expression, Operator.Value... parameters) {
            this.expression = expression;
            this.parameters = Arrays.asList(parameters);
        }

        public SqlExpression(String expression, List<Operator.Value> parameters) {
            this.expression = expression;
            this.parameters = parameters;
        }

        public String getExpression() {
            return expression;
        }

        public List<Operator.Value> getParameters() {
            return parameters;
        }
    }

    public Expression parse(Operator operator);
}
