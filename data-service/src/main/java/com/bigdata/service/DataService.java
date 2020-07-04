package com.bigdata.service;

import com.bigdata.core.query.SchemaUtil;
import com.fasterxml.jackson.databind.JsonNode;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DataService {

    @Autowired
    private GraphQL graphQL;

    public JsonNode lookupData(String query, Map<String, Object> parameters) {
        ExecutionResult result = graphQL.execute(query, null, null, parameters);
        List<GraphQLError> errors = result.getErrors();
        if (errors == null || errors.isEmpty()) {
            Object data = result.getData();
            return SchemaUtil.getMapper().convertValue(data, JsonNode.class);
        } else {
            throw new IllegalStateException(this.getMessage(errors));
        }
    }

    private String getMessage(List<GraphQLError> errors) {
        return errors.stream().map(error -> error.getErrorType() + " - " + error.getMessage()).reduce((e1, e2) -> e1 + ", " + e2).get();
    }

}
