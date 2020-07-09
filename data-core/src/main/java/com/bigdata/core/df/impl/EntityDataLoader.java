package com.bigdata.core.df.impl;

import com.bigdata.core.df.intf.BaseDataLoader;
import com.bigdata.core.query.DataQuery;

import java.util.Map;

public class EntityDataLoader extends BaseDataLoader<Map<String, Object>> {


    @Override
    public Map<String, Object> fetch(DataQuery query) {
       return null;
        // return super.dataDAO.execute(query.getQuery(), query.getParameters());
    }
}
