package test;

import java.util.List;
import java.util.Map;

public class DataQuery {
    private String query;
    private List<Object> parameters;
    private Map<String, String> mapping;

    public DataQuery(String query, List<Object> parameters, Map<String, String> mapping) {
        this.query = query;
        this.parameters = parameters;
        this.mapping = mapping;
    }

    public String getQuery() {
        return query;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public Map<String, String> getMapping() {
        return mapping;
    }
}
