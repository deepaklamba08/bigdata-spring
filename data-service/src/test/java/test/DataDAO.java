package test;

import java.util.List;
import java.util.Map;

public interface DataDAO {

    public Map<String, Object> execute(String query, List<Object> parameters);
}
