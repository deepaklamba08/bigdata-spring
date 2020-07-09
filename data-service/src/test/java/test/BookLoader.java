package test;

import com.bigdata.core.df.intf.BaseDataLoader;
import com.bigdata.core.query.DataQuery;
import com.bigdata.dao.intf.impl.JdbcDAO;

import java.util.Map;

public class BookLoader extends BaseDataLoader<Book> {

    @Override
    public Book fetch(DataQuery query) {
        JdbcDAO jdbcDAO = (JdbcDAO) this.dataDAO;
        Map<String, Object> data = jdbcDAO.execute(query.getQuery(), query.getParameters());

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
