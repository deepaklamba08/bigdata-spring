package com.bigdata.core;

import com.bigdata.core.df.impl.MultipleObjectDataLoader;
import com.bigdata.core.df.impl.SingleObjectDataLoader;
import com.bigdata.core.df.intf.DataFetcherFactory;
import com.bigdata.core.query.Catalog;
import com.bigdata.core.query.QueryBuilder;
import com.bigdata.core.query.SchemaUtil;
import com.bigdata.core.query.impl.SqlQueryBuilder;
import com.bigdata.dao.intf.impl.DataDAOFactory;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class TestGraphQl {

    public static void main(String[] args) throws IOException {

        String schemaFile = TestGraphQl.class.getClassLoader().getResource("schema.txt").getFile();
        TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(new File(schemaFile));
        RuntimeWiring runtimeWiring = buildRuntimeWiring();

        GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
        GraphQL graphQL = GraphQL.newGraphQL(graphQLSchema).build();

        // String query = "{bookBy(title:\"a lion\" authorName : \"shane\"){title publisher author{name age}}}";

        String query = "{bookByIdV2(id:[\"1\",\"2\"],pageSize : 3){title publisher}}";
        ExecutionResult executionResult = graphQL.execute(query);


        System.out.println((Object) executionResult.getData());
        System.out.println(executionResult.getErrors());
        System.out.println(executionResult.getExtensions());

    }

    private static RuntimeWiring buildRuntimeWiring() throws IOException {

        String catalogFile = TestGraphQl.class.getClassLoader().getResource("db_schema.json").getFile();
        Catalog catalog = SchemaUtil.readCatalog(new File(catalogFile));
        QueryBuilder queryBuilder = new SqlQueryBuilder(catalog);

        EnumMap<DataFetcherFactory.FetcherType, String> fetcherTypes = new EnumMap<>(DataFetcherFactory.FetcherType.class);
        fetcherTypes.put(DataFetcherFactory.FetcherType.SingleObject, SingleObjectDataLoader.class.getName());
        fetcherTypes.put(DataFetcherFactory.FetcherType.MultipleObject, MultipleObjectDataLoader.class.getName());


        File dbFile = new ClassPathResource("test_data/test_sensor_data.mv.db").getFile();
        StringBuilder dbUrl = new StringBuilder("jdbc:h2:file:").append(dbFile.getAbsolutePath());


        Map<String, String> cred = new HashMap<>(5);
        cred.put("dao.type", "jdbc");
        cred.put("db.driver.class", "org.h2.Driver");
        cred.put("db.url", dbUrl.substring(0, dbUrl.length() - 6));
        cred.put("db.user", "sa");
        cred.put("db.password", "password");

        DataDAOFactory dataDAOFactory = new DataDAOFactory(cred);
        DataFetcherFactory fetcherFactory = new DataFetcherFactory(queryBuilder, dataDAOFactory.createDAO(), catalog, fetcherTypes);

        RuntimeWiring.Builder wiring = RuntimeWiring.newRuntimeWiring();
        TypeRuntimeWiring.Builder builder = new TypeRuntimeWiring.Builder();
        builder.typeName("Query");

        catalog.getDataloaders().forEach((key, value) ->
                builder.dataFetcher(key, fetcherFactory.createDataFetcher(DataFetcherFactory.FetcherType.valueOf(value)))
        );
        return wiring.type(builder.build()).build();
    }

}
