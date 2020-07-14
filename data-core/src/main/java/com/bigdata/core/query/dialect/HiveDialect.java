package com.bigdata.core.query.dialect;

public class HiveDialect implements Dialect {

    @Override
    public String getQuotedString(String attribute) {
        return attribute;
    }

    @Override
    public String getPaginationString(int pageSize, int pageNumber) {
        if (pageSize > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append("LIMIT").append(pageSize);
            if (pageNumber > 1) {
                builder.append(" ").append(pageNumber - 1);
            }
            return builder.toString();
        }else{
            return null;
        }
    }
}
