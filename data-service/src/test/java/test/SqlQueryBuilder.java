package test;

import graphql.language.*;
import org.springframework.data.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class SqlQueryBuilder implements QueryBuilder {

    private SchemaProvider schemaProvider;

    public SqlQueryBuilder(SchemaProvider schemaProvider) {
        this.schemaProvider = schemaProvider;
    }

    @Override
    public DataQuery buildQuery(Map<String, List<String>> selectionSet, Map<String, List<Pair<String, Value>>> expressions) {

        Map<String, String> inverseMapping = new HashMap<>();

        List<String> projections = selectionSet.entrySet().stream().map(pair -> {
            String tableAlias = this.schemaProvider.getQueryAliases(pair.getKey());
            List<String> columnAliases = pair.getValue();

            SchemaProvider.TableSchema table = schemaProvider.getTableSchema(tableAlias);
            columnAliases.forEach(alias -> {
                inverseMapping.put(alias, table.getColumnName(alias));
            });

            String projection = columnAliases.stream().map(columnAlias -> table.getAlias() + "." + table.getColumnName(columnAlias) + " as \"" + columnAlias+"\"").reduce((v1, v2) -> v1 + ", " + v2).get();
            return projection;
        }).collect(Collectors.toList());


        StringBuilder query = new StringBuilder("SELECT ");
        query.append(projections.stream().reduce((v1, v2) -> v1 + ", " + v2).get());
        query.append(" FROM ").append(this.buildSelectionSet(selectionSet.keySet()));
        query.append(" WHERE 1=1 ");
        List<Object> parameters = new ArrayList<>();
        expressions.entrySet().forEach(pair -> {
            String tableAlias = this.schemaProvider.getQueryAliases(pair.getKey());
            SchemaProvider.TableSchema table = schemaProvider.getTableSchema(tableAlias);
            List<Pair<String, Object>> expressionPairs = pair.getValue().stream().map(expression -> this.parseExpression(table.getColumnName(expression.getFirst()), tableAlias, expression.getSecond())).collect(Collectors.toList());
            if (!expressionPairs.isEmpty()) {
                String whereClause = expressionPairs.stream().map(ePair -> ePair.getFirst()).reduce((p1, p2) -> p1 + " AND " + p2).get();
                query.append(" AND (").append(whereClause).append(")");
                parameters.addAll(expressionPairs.stream().map(ePair -> ePair.getSecond()).collect(Collectors.toList()));
            }
        });

        return new DataQuery(query.toString(), parameters, inverseMapping);
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
            SchemaProvider.TableSchema tableSchema = this.schemaProvider.getTableSchema(aliasesItr.next());
            return tableSchema.getTableName() + " " + tableSchema.getAlias();
        } else {
            String key = tableAliases.stream().sorted(String::compareTo).reduce((a1, a2) -> a1 + "~" + a2).get();
            return this.schemaProvider.getJoinExpression(key);
        }
    }

}
