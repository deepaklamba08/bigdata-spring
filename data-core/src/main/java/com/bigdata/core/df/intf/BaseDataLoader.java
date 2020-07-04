package com.bigdata.core.df.intf;

import com.bigdata.core.query.DataQuery;
import com.bigdata.core.query.QueryBuilder;
import com.bigdata.dao.intf.DataDAO;
import graphql.language.*;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.data.util.Pair;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseDataLoader<T> implements DataFetcher<T> {
    private QueryBuilder queryBuilder;
    protected DataDAO dataDAO;

    public void init(QueryBuilder queryBuilder,DataDAO dataDAO) {
        this.queryBuilder = queryBuilder;
        this.dataDAO=dataDAO;
    }

    @Override
    public T get(DataFetchingEnvironment environment) {
        Map<String, List<String>> projections = this.scanProjections(environment.getFields());
        Map<String, List<Pair<String, Value>>> expressions = this.scanExpressions(environment.getFields());
        DataQuery query = queryBuilder.buildQuery(projections, expressions);
        return this.fetch(query);
    }

    public abstract T fetch(DataQuery query);

    private Map<String, List<String>> scanProjections(List<Field> fields) {
        Field field = fields.get(0);
        List<Selection> selections = field.getSelectionSet().getSelections();
        Function<Field, String> mapper = (f) -> f.getName();
        BinaryOperator<Map<String, List<String>>> reducer = this.createReducer();
        Map<String, List<String>> projections = selections.stream().map(s -> parse(s, field.getName(), mapper, reducer)).reduce(reducer).get();
        return projections;
    }

    private Map<String, List<Pair<String, Value>>> scanExpressions(List<Field> fields) {
        Field field = fields.get(0);
        List<Selection> selections = field.getSelectionSet().getSelections();
        Function<Field, List<Argument>> mapper = (f) -> f.getArguments();
        BinaryOperator<Map<String, List<List<Argument>>>> reducer = this.createReducer();
        Map<String, List<List<Argument>>> expressions = selections.stream().map(s -> parse(s, field.getName(), mapper, reducer)).reduce(reducer).get();

        Map<String, List<Pair<String, Value>>> expressionMap = expressions.entrySet().stream().map(entry -> {
            List<Pair<String, Value>> values = entry.getValue().stream().flatMap(arguments -> arguments.stream().map(argument -> Pair.of(argument.getName(), argument.getValue()))).collect(Collectors.toList());
            return Pair.of(entry.getKey(), values);
        }).collect(Collectors.toMap(k -> k.getFirst(), k -> k.getSecond()));

        List<Pair<String, Value>> args = field.getArguments().stream().map(argument -> Pair.of(argument.getName(), argument.getValue())).collect(Collectors.toList());
        List<Pair<String, Value>> old = expressionMap.get(field.getName());
        old.addAll(args);
        expressionMap.put(field.getName(), old);
        return expressionMap;
    }

    private <T> Map<String, List<T>> parse(Selection selection, String source, Function<Field, T> mapper, BinaryOperator<Map<String, List<T>>> reducer) {
        Field sfield = (Field) selection;
        SelectionSet childSelections = sfield.getSelectionSet();
        if (childSelections != null) {
            return childSelections.getSelections().stream().map(s -> parse(s, sfield.getName(), mapper, reducer)).reduce(reducer).get();
        } else {
            Map<String, List<T>> op = new LinkedHashMap<>(1);
            op.put(source, Collections.singletonList(mapper.apply(sfield)));
            return op;
        }
    }

    private <T> BinaryOperator<Map<String, List<T>>> createReducer() {
        BinaryOperator<Map<String, List<T>>> reducer = (mapOne, mapTwo) -> {
            Set<String> keys = new HashSet<>(mapOne.keySet());
            keys.addAll(mapTwo.keySet());
            Map<String, List<T>> op = new LinkedHashMap<>(keys.size());
            for (String key : keys) {
                List<T> values = new ArrayList<>();
                List<T> v1 = mapOne.get(key);
                if (v1 != null) {
                    values.addAll(v1);
                }
                v1 = mapTwo.get(key);
                if (v1 != null) {
                    values.addAll(v1);
                }
                op.put(key, values);
            }
            return op;
        };
        return reducer;
    }


}
