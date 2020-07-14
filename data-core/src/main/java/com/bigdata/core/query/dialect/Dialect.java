package com.bigdata.core.query.dialect;

public interface Dialect {

    public String getQuotedString(String attribute);

    public String getPaginationString(int pageSize, int pageNumber);
}
