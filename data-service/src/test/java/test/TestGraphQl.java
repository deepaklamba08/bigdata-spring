package test;

import com.bigdata.core.df.intf.BaseDataLoader;
import com.bigdata.core.query.Catalog;
import com.bigdata.core.query.QueryBuilder;
import com.bigdata.core.query.SchemaUtil;
import com.bigdata.core.query.impl.SqlQueryBuilder;
import com.bigdata.dao.intf.DataDAO;
import com.bigdata.dao.intf.impl.JdbcDAO;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.File;
import java.io.IOException;

public class TestGraphQl {

    public static void main(String[] args) throws IOException {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:file:D:\\tools\\h2\\test_data\\test_sensor_data");
        dataSource.setUsername("sa");
        dataSource.setPassword("password");

        Catalog catalog = SchemaUtil.readCatalog(new File("D:\\dev\\learning\\bigdata-spring\\data-service\\src\\test\\resources\\db_schema.json"));
        DataDAO dataDAO = new JdbcDAO(new JdbcTemplate(dataSource));
        QueryBuilder queryBuilder = new SqlQueryBuilder(catalog);

        File schemaFile = new File("D:\\dev\\learning\\bigdata-spring\\data-service\\src\\test\\resources\\schema.txt");
        TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(schemaFile);
        RuntimeWiring runtimeWiring = buildRuntimeWiring(dataDAO, queryBuilder, catalog);


        GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
        GraphQL graphQL = GraphQL.newGraphQL(graphQLSchema).build();

       // String query = "{bookBy(title:\"a lion\" authorName : \"shane\"){title publisher author{name age}}}";

        String query="{bookById(id:[\"1\",\"2\"]){title publisher}}";
        ExecutionResult executionResult = graphQL.execute(query);


        System.out.println((Object) executionResult.getData());
        System.out.println(executionResult.getErrors());
        System.out.println(executionResult.getExtensions());

    }

    private static RuntimeWiring buildRuntimeWiring(DataDAO dataDAO, QueryBuilder queryBuilder, Catalog catalog) {
        BaseDataLoader<Book> dataLoader = new BookLoader();
        dataLoader.init(queryBuilder, catalog, dataDAO);
        return RuntimeWiring.newRuntimeWiring()
                .type("Query", typeWiring -> typeWiring
                        .dataFetcher("allBooks", new AllBooksLoader())
                        .dataFetcher("book", dataLoader)
                        .dataFetcher("bookBy", dataLoader)
                        .dataFetcher("bookById", dataLoader)).build();
    }

}
