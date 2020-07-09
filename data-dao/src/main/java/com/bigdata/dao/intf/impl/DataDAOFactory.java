package com.bigdata.dao.intf.impl;


import com.bigdata.dao.intf.DataDAO;
import com.bigdata.dao.intf.DataQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.Map;

public class DataDAOFactory {

    private static final String TYPE = "dao.type";
    private static final String TYPE_JDBC = "jdbc";
    private Map<String, String> config;

    public DataDAOFactory(Map<String, String> config) {
        this.config = config;
    }

    public DataDAO<DataQuery> createDAO() {
        String type = this.config.get(TYPE);
        if (TYPE_JDBC.equals(type)) {
            return new JdbcDAO(this.createJdbcTemplate());
        } else {
            throw new IllegalStateException("DAO type is invalid - " + type);
        }
    }

    private JdbcTemplate createJdbcTemplate() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(this.config.get("db.driver.class"));
        dataSource.setUrl(this.config.get("db.url"));
        dataSource.setUsername(this.config.get("db.user"));
        dataSource.setPassword(this.config.get("db.password"));

        return new JdbcTemplate(dataSource);
    }

}
