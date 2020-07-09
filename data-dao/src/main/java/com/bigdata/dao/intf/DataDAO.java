package com.bigdata.dao.intf;

import com.bigdata.dao.model.Query;

import java.util.List;
import java.util.Map;


public interface DataDAO {

    public Map<String,Object> lookupRecord(Query query);

    public List<Map<String,Object>> lookup(Query query);

}
