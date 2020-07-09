package com.bigdata.core.df.impl;

import com.bigdata.core.df.intf.BaseDataLoader;
import com.bigdata.core.query.DataQuery;
import com.bigdata.dao.intf.impl.JdbcDAO;

import java.util.Map;

public class EntityDataLoader extends BaseDataLoader<Map<String, Object>> {

    @Override
    public Map<String, Object> fetch(DataQuery query) {

        JdbcDAO jdbcDAO = (JdbcDAO) this.dataDAO;
        Map<String, Object> data = jdbcDAO.execute(query.getQuery(), query.getParameters());

        return data;
    }
}
