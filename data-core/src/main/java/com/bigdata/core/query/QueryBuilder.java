package com.bigdata.core.query;

import com.bigdata.core.op.LookupExpression;

public interface QueryBuilder {
    DataQuery buildQuery(LookupExpression expression);
}
