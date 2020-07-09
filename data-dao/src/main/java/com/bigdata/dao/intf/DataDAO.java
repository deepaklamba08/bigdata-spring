package com.bigdata.dao.intf;

import com.bigdata.dao.model.Query;

import java.util.List;
import java.util.Map;


public interface DataDAO<T extends Query> {

    public Map<String,Object> lookupRecord(T query);

    public List<Map<String,Object>> lookup(T query);

}
