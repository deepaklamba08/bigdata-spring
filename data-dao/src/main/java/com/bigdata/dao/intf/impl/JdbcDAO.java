package com.bigdata.dao.intf.impl;

import com.bigdata.dao.intf.DataDAO;
import com.bigdata.dao.intf.DataQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.util.List;
import java.util.Map;

public class JdbcDAO implements DataDAO<DataQuery> {

    private Logger logger;
    private JdbcTemplate jdbcTemplate;

    public JdbcDAO(JdbcTemplate jdbcTemplate) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<String, Object> lookupRecord(DataQuery dataQuery) {
        logger.debug("Sql query - {}, parameters - {}", dataQuery.getQuery(), dataQuery.getParameters());
        ResultSetExtractor<Map<String, Object>> extractor = resultSet -> resultSet.next() ? new ColumnMapRowMapper().mapRow(resultSet, 0) : null;
        Map<String, Object> data = this.jdbcTemplate.query(dataQuery.getQuery(), dataQuery.getParameters().toArray(), extractor);
        return data;

    }

    @Override
    public List<Map<String, Object>> lookup(DataQuery dataQuery) {
        return this.jdbcTemplate.queryForList(dataQuery.getQuery(), dataQuery.getParameters().toArray());
    }

}
