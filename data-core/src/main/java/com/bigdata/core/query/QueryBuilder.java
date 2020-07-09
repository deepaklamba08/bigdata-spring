package com.bigdata.core.query;

import com.bigdata.core.op.LookupExpression;
import com.bigdata.dao.model.Query;

public interface QueryBuilder {
    Query buildQuery(LookupExpression expression);
}
