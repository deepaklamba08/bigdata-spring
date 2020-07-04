package test;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;

public class AllBooksLoader implements DataFetcher<List<Book>> {
    @Override
    public List<Book> get(DataFetchingEnvironment dataFetchingEnvironment) {
        System.out.print(dataFetchingEnvironment);
        return null;
    }
}
