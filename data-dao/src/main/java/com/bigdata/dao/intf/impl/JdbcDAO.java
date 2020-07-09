package com.bigdata.dao.intf.impl;

import com.bigdata.dao.intf.DataDAO;
import com.bigdata.dao.intf.DataQuery;
import com.bigdata.dao.model.Query;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.util.List;
import java.util.Map;

public class JdbcDAO implements DataDAO {

    private JdbcTemplate jdbcTemplate;

    public JdbcDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<String, Object> lookupRecord(Query query) {
        DataQuery dataQuery = (DataQuery) query;
        ResultSetExtractor<Map<String, Object>> extractor = resultSet -> resultSet.next() ? new ColumnMapRowMapper().mapRow(resultSet, 0) : null;
        Map<String, Object> data = this.jdbcTemplate.query(dataQuery.getQuery(), dataQuery.getParameters().toArray(), extractor);
        return data;

    }

    @Override
    public List<Map<String, Object>> lookup(Query query) {
        DataQuery dataQuery = (DataQuery) query;
        return this.jdbcTemplate.queryForList(dataQuery.getQuery(), dataQuery.getParameters().toArray());
    }

}
