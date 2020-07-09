package com.bigdata.core.df.impl;

import com.bigdata.core.df.intf.BaseDataLoader;
import com.bigdata.dao.intf.DataQuery;
import com.bigdata.dao.intf.impl.JdbcDAO;
import com.bigdata.dao.model.Query;

import java.util.Map;

public class SingleObjectDataLoader extends BaseDataLoader<Map<String, Object>> {

    @Override
    public Map<String, Object> fetch(Query query) {
        Map<String, Object> data = this.dataDAO.lookupRecord(query);
        return data;
    }
}
