package com.bigdata.conf;

import com.bigdata.core.df.intf.DataFetcherFactory;
import com.bigdata.core.query.Catalog;
import com.bigdata.core.query.QueryBuilder;
import com.bigdata.core.query.SchemaUtil;
import com.bigdata.core.query.impl.SqlQueryBuilder;
import com.bigdata.dao.intf.DataDAO;
import com.bigdata.dao.intf.impl.DataDAOFactory;
import com.bigdata.dao.intf.impl.JdbcDAO;
import com.bigdata.util.ServiceProperties;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

@Configuration
public class BeanProvider {

    @Autowired
    private ServiceProperties serviceProperties;


    @Bean(name = "deviceDAO")
    @Scope("singleton")
    public GraphQL graphQL() throws IOException {
        File schemaFile = new File(serviceProperties.getGraphqlSchemaFilePath());
        TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(schemaFile);
        RuntimeWiring runtimeWiring = buildRuntimeWiring();
        GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
        GraphQL graphQL = GraphQL.newGraphQL(graphQLSchema).build();
        return graphQL;
    }

    private RuntimeWiring buildRuntimeWiring() throws IOException {
        Catalog catalog = SchemaUtil.readCatalog(new File(serviceProperties.getDatabaseMappingFilePath()));
        QueryBuilder queryBuilder = new SqlQueryBuilder(catalog);

        EnumMap<DataFetcherFactory.FetcherType, String> fetcherTypes = new EnumMap<>(DataFetcherFactory.FetcherType.class);
        fetcherTypes.put(DataFetcherFactory.FetcherType.SingleObject, this.serviceProperties.getSingleObjectProvider());
        fetcherTypes.put(DataFetcherFactory.FetcherType.MultipleObject, this.serviceProperties.getMultipleObjectProvider());

        DataDAOFactory dataDAOFactory = new DataDAOFactory(this.serviceProperties.getDbCred());
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
