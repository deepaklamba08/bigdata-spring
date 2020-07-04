package test;

import java.util.Map;

public class BookLoader extends BaseDataLoader<Book> {

    private DataDAO dataDAO;

    public BookLoader(DataDAO dataDAO, QueryBuilder queryBuilder) {
        super(queryBuilder);
        this.dataDAO = dataDAO;
    }

    @Override
    public Book fetch(DataQuery query) {
        Map<String, Object> data = dataDAO.execute(query.getQuery(), query.getParameters());

        return new Book(asString(data, "isn"),
                asString(data, "title"),
                asString(data, "publisher"),
                asString(data, "title"),
                asString(data, "author"));
    }

    private String asString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (key != null) {
            return (String) value;
        } else {
            return null;
        }
    }
}
