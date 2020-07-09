package test;

import com.bigdata.core.df.intf.BaseDataLoader;
import com.bigdata.dao.intf.DataQuery;
import com.bigdata.dao.intf.impl.JdbcDAO;
import com.bigdata.dao.model.Query;

import java.util.Map;

public class BookLoader extends BaseDataLoader<Book> {

    @Override
    public Book fetch(Query query) {
        Map<String, Object> data =  this.dataDAO.lookupRecord(query);

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
