package com.bigdata.core.query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SchemaUtil {

    private static final String JOIN_EXPRESSIONS = "joinExpressions";
    private static final String QUERY_ALIASES = "queryAliases";
    private static final String TABLES = "tables";

    private static ObjectMapper mapper = new ObjectMapper();

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static SchemaProvider buildSchema(File schemaFile) throws IOException {

        JsonNode schemaNode = mapper.readTree(schemaFile);
        Map<String, String> joinExpressions = readAsMap(schemaNode.get(JOIN_EXPRESSIONS));
        Map<String, String> queryAliases = readAsMap(schemaNode.get(QUERY_ALIASES));

        SchemaProvider schemaProvider = new SchemaProvider();
        schemaProvider.setJoinExpressions(joinExpressions);
        schemaProvider.setQueryAliases(queryAliases);
        ArrayNode tables = (ArrayNode) schemaNode.get(TABLES);
        for (JsonNode table : tables) {
            schemaProvider.addTableSchema(readTableSchema(table));
        }

        return schemaProvider;
    }

    private static SchemaProvider.TableSchema readTableSchema(JsonNode table) {

        String tableName = table.get("tableName").asText();
        String alias = table.get("alias").asText();
        Map<String, String> columns = readAsMap(table.get("columns"));
        return new SchemaProvider.TableSchema(tableName, alias, columns);
    }

    public static Map<String, String> readAsMap(File dataFile) throws IOException {
        JsonNode jsonNode = mapper.readTree(dataFile);

        if (jsonNode == null || jsonNode.isNull()) {
            return null;
        }
        ObjectNode objectNode = (ObjectNode) jsonNode;
        Iterator<String> fields = objectNode.fieldNames();
        Map<String, String> mapping = new HashMap<>();
        while (fields.hasNext()) {
            String field = fields.next();
            mapping.put(field, jsonNode.get(field).asText());
        }
        return mapping;
    }

    private static Map<String, String> readAsMap(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isNull()) {
            return null;
        }
        ObjectNode objectNode = (ObjectNode) jsonNode;
        Iterator<String> fields = objectNode.fieldNames();
        Map<String, String> mapping = new HashMap<>();
        while (fields.hasNext()) {
            String field = fields.next();
            mapping.put(field, jsonNode.get(field).asText());
        }
        return mapping;
    }
}
