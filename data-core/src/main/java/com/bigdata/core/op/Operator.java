package com.bigdata.core.op;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface Operator {

    public abstract class LogicalOperator implements Operator {

        private final Collection<Operator> operators;

        public LogicalOperator(Operator... operator) {
            this.operators = Arrays.asList(operator);
        }

        public LogicalOperator(Collection<Operator> operators) {
            this.operators = operators;
        }

        public Collection<Operator> getOperators() {
            return operators;
        }


    }

    public abstract class Value {

        public abstract Object getValue();
    }

    public abstract class SingleValue extends Value {

    }

    public abstract class MultiValue extends Value {
    }


    public class ArrayValue extends Value {

        private final List<SingleValue> values;

        public ArrayValue(SingleValue... values) {
            this.values = Arrays.asList(values);
        }

        public ArrayValue(List<SingleValue> values) {
            this.values = values;
        }

        @Override
        public List<SingleValue> getValue() {
            return values;
        }
    }

    public class StringValue extends SingleValue {

        private final String value;

        public StringValue(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return this.value;
        }
    }

    public class IntValue extends SingleValue {

        private final int value;

        public IntValue(int value) {
            this.value = value;
        }

        @Override
        public Integer getValue() {
            return this.value;
        }
    }

    public class NumberValue extends SingleValue {

        private final Number value;

        public NumberValue(Number value) {
            this.value = value;
        }

        @Override
        public Number getValue() {
            return this.value;
        }
    }


    public class BooleanValue extends SingleValue {

        private final Boolean value;

        public BooleanValue(Boolean value) {
            this.value = value;
        }

        @Override
        public Boolean getValue() {
            return this.value;
        }
    }

    public class DateValue extends SingleValue {

        private final Date value;

        public DateValue(Date value) {
            this.value = value;
        }

        @Override
        public Date getValue() {
            return this.value;
        }
    }

    public abstract class RelationalOperator implements Operator {

        private final String key;
        protected final Value value;

        public RelationalOperator(String key, Value value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public Value getValue() {
            return value;
        }

    }

    public class And extends LogicalOperator {

        public And(Operator... operator) {
            super(operator);
        }

        public And(Collection<Operator> operator) {
            super(operator);
        }
    }

    public class Or extends LogicalOperator {
        public Or(Operator... operator) {
            super(operator);
        }
    }

    public class Equals extends RelationalOperator {

        public Equals(String key, Value value) {
            super(key, value);
        }
    }

    public class NotEquals extends RelationalOperator {

        public NotEquals(String key, Value value) {
            super(key, value);
        }
    }

    public class Gt extends RelationalOperator {

        public Gt(String key, Value value) {
            super(key, value);
        }
    }

    public class GtEq extends RelationalOperator {

        public GtEq(String key, Value value) {
            super(key, value);
        }
    }

    public class Lt extends RelationalOperator {

        public Lt(String key, Value value) {
            super(key, value);
        }
    }

    public class LtEq extends RelationalOperator {

        public LtEq(String key, Value value) {
            super(key, value);
        }
    }

    public class In extends RelationalOperator {

        public In(String key, ArrayValue value) {
            super(key, value);
        }

        @Override
        public ArrayValue getValue() {
            return (ArrayValue) super.value;
        }
    }

    public class NotIn extends RelationalOperator {

        public NotIn(String key, ArrayValue value) {
            super(key, value);
        }

        @Override
        public ArrayValue getValue() {
            return (ArrayValue) super.value;
        }

    }

}
