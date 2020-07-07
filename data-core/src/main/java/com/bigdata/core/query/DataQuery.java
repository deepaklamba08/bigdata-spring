package com.bigdata.core.query;

import java.util.List;
import java.util.Map;

public class DataQuery {
    private String query;
    private List<Object> parameters;

    public DataQuery(String query, List<Object> parameters) {
        this.query = query;
        this.parameters = parameters;
    }

    public String getQuery() {
        return query;
    }

    public List<Object> getParameters() {
        return parameters;
    }

}
