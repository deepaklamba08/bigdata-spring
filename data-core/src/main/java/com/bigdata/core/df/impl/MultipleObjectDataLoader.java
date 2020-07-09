package com.bigdata.core.df.impl;

import com.bigdata.core.df.intf.BaseDataLoader;
import com.bigdata.dao.intf.impl.JdbcDAO;
import com.bigdata.dao.model.Query;

import java.util.List;
import java.util.Map;

public class MultipleObjectDataLoader extends BaseDataLoader<List<Map<String, Object>>> {

    @Override
    public List<Map<String, Object>> fetch(Query query) {
        JdbcDAO jdbcDAO = (JdbcDAO) this.dataDAO;
        return jdbcDAO.lookup(query);
    }
}
