package com.bigdata.model;

import java.util.Map;

public class DataRequest {

    private String query;
    private Map<String,Object> parameters;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
