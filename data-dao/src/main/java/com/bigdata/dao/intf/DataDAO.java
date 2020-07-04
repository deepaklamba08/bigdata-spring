package com.bigdata.dao.intf;

import java.util.List;
import java.util.Map;

public interface DataDAO {

    public Map<String, Object> execute(String query, List<Object> parameters);

    public List<Map<String, Object>> executeQuery(String query, List<Object> parameters);
}
