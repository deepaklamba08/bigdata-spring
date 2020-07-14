package com.bigdata.service;

import com.bigdata.core.query.SchemaUtil;
import com.bigdata.model.RequestResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DataService {

    private Logger logger;

    public DataService() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @Autowired
    private GraphQL graphQL;

    public RequestResult<JsonNode> lookupData(String query, Map<String, Object> parameters) {
        logger.debug("Executing : DataService.lookupData()");
        ExecutionResult result = graphQL.execute(query, null, null, parameters);
        List<GraphQLError> errors = result.getErrors();
        if (errors == null || errors.isEmpty()) {
            Object data = result.getData();
            return new RequestResult<>(200, "success", SchemaUtil.getMapper().convertValue(data, JsonNode.class));
        } else {
            ObjectNode resultNode = SchemaUtil.getMapper().createObjectNode();
            resultNode.put("errorMessage", "search operation failed due to invalid request");
            logger.error(this.getMessage(errors));
            return new RequestResult<>(400, "failed", resultNode);
        }
    }

    private String getMessage(List<GraphQLError> errors) {
        return errors.stream().map(error -> error.getErrorType() + " - " + error.getMessage()).reduce((e1, e2) -> e1 + ", " + e2).get();
    }

}
