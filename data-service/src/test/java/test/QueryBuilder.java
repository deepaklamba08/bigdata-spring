package test;

import graphql.language.Value;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;

public interface QueryBuilder {
    DataQuery buildQuery(Map<String, List<String>> selectionSet, Map<String, List<Pair<String, Value>>> expressions);
}
