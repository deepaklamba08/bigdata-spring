package test;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class WriteSchema {

    public static void main(String[] args) throws IOException {

        SchemaProvider schema = getSchemaProvider();

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.writeValue(new File("D:\\dev\\learning\\bigdata-spring\\data-service\\src\\main\\resources\\db_schema.json"), schema);
    }

    private static SchemaProvider getSchemaProvider() {
        SchemaProvider.TableSchema entitySchema = new SchemaProvider.TableSchema();

        entitySchema.setAlias("entity");
        entitySchema.setTableName("ENTITY");
        entitySchema.addColumn("id", "ID");
        entitySchema.addColumn("type", "TYPE");
        entitySchema.addColumn("data", "DATA");

        SchemaProvider schemaProvider = new SchemaProvider();
        schemaProvider.addTableSchema(entitySchema);
        schemaProvider.addQueryAliases("lookupEntity", "entity");
        schemaProvider.addQueryAliases("lookupEntityByType", "entity");

        return schemaProvider;
    }
}
