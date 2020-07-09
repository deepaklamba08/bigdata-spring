package com.bigdata.dao.intf;

import com.bigdata.dao.model.Query;

import java.util.List;
import java.util.Map;

public class DataQuery implements Query {
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

    @Override
    public String toString() {
        return query + '\n' + parameters;
    }
}
