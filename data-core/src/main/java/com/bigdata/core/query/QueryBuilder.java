package com.bigdata.core.query;

import com.bigdata.core.op.LookupExpression;
import graphql.language.Value;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;

public interface QueryBuilder {
    DataQuery buildQuery(LookupExpression expression);
}
