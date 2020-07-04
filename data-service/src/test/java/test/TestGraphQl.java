package test;

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

public class TestGraphQl {

    public static void main(String[] args) {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:file:D:\\tools\\h2\\test_data\\test_sensor_data");
        dataSource.setUsername("sa");
        dataSource.setPassword("password");

        SchemaProvider schemaProvider = getSchemaProvider();

        DataDAO dataDAO = new JdbcDAO(new JdbcTemplate(dataSource));
        QueryBuilder queryBuilder = new SqlQueryBuilder(schemaProvider);

        File schemaFile = new File("D:\\dev\\learning\\bigdata-spring\\data-service\\src\\test\\resources\\schema.txt");
        TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(schemaFile);
        RuntimeWiring runtimeWiring = buildRuntimeWiring(dataDAO, queryBuilder);


        GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
        GraphQL graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    /*
    {
   book(id:"1001"){
      title
   }

   allBooks{
       isn
       title
       author
       publisher
       publishedDate
   }
}
     */
//        String query = "{book(id:\"1001\"){title publisher author{name age}}}";

        String query = "{bookBy(title:\"a lion\" authorName : \"shane\"){title publisher author{name age}}}";
        ExecutionResult executionResult = graphQL.execute(query);


        System.out.println((Object) executionResult.getData());
        System.out.println(executionResult.getErrors());
        System.out.println(executionResult.getExtensions());

    }

    private static SchemaProvider getSchemaProvider() {
        SchemaProvider.TableSchema bookTableSchema = new SchemaProvider.TableSchema();

        bookTableSchema.setAlias("book");
        bookTableSchema.setTableName("BOOK");
        bookTableSchema.addColumn("id", "ID");
        bookTableSchema.addColumn("isn", "ISN");
        bookTableSchema.addColumn("title", "TITLE");
        bookTableSchema.addColumn("authorName", "AUTHOR_NAME");
        bookTableSchema.addColumn("publisher", "PUBLISHER");
        bookTableSchema.addColumn("publishedDate", "PUBLISH_DATE");

        SchemaProvider.TableSchema authorTableSchema = new SchemaProvider.TableSchema();
        authorTableSchema.setAlias("author");
        authorTableSchema.setTableName("AUTHOR");
        authorTableSchema.addColumn("name", "NAME");
        authorTableSchema.addColumn("age", "AGE");

        SchemaProvider schemaProvider = new SchemaProvider();
        schemaProvider.addTableSchema(bookTableSchema);
        schemaProvider.addTableSchema(authorTableSchema);
        schemaProvider.addQueryAliases("book", "book");
        schemaProvider.addQueryAliases("bookBy", "book");
        schemaProvider.addQueryAliases("author", "author");

        schemaProvider.addJoinExpression("author~book", "AUTHOR author inner join BOOK book on author.id=book.author_id");
        schemaProvider.addJoinExpression("author~bookBy","AUTHOR author inner join BOOK book on author.id=book.author_id");
        return schemaProvider;
    }

    private static RuntimeWiring buildRuntimeWiring(DataDAO dataDAO, QueryBuilder queryBuilder) {
        return RuntimeWiring.newRuntimeWiring()
                .type("Query", typeWiring -> typeWiring
                        .dataFetcher("allBooks", new AllBooksLoader())
                        .dataFetcher("book", new BookLoader(dataDAO, queryBuilder))
                        .dataFetcher("bookBy", new BookLoader(dataDAO, queryBuilder))).build();
    }

}
