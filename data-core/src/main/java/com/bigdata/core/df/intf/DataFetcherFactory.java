package com.bigdata.core.df.intf;

import com.bigdata.core.query.Catalog;
import com.bigdata.core.query.QueryBuilder;
import com.bigdata.dao.intf.DataDAO;

import java.util.HashMap;
import java.util.Map;

public class DataFetcherFactory {

    private final QueryBuilder queryBuilder;
    private final DataDAO dataDAO;
    private final Catalog catalog;

    private Map<String, BaseDataLoader<?>> cache;

    public DataFetcherFactory(QueryBuilder queryBuilder, DataDAO dataDAO, Catalog catalog) {
        this.queryBuilder = queryBuilder;
        this.dataDAO = dataDAO;
        this.catalog = catalog;
        this.cache = new HashMap<>();
    }

    public <T> BaseDataLoader<T> createDataFetcher(String implClass) {
        BaseDataLoader<?> baseDataLoader = this.cache.get(implClass);
        if (baseDataLoader != null) {
            return (BaseDataLoader<T>) baseDataLoader;
        } else {
            BaseDataLoader<T> tBaseDataLoader = this.createNew(implClass);
            this.cache.put(implClass, tBaseDataLoader);
            return tBaseDataLoader;
        }
    }

    private <T> BaseDataLoader<T> createNew(String implClass) {

        try {
            Class<?> clzz = Class.forName(implClass);
            Object dataLoader = clzz.newInstance();
            if (dataLoader instanceof BaseDataLoader<?>) {
                BaseDataLoader<T> tBaseDataLoader = (BaseDataLoader<T>) dataLoader;
                tBaseDataLoader.init(queryBuilder, catalog, dataDAO);
                return tBaseDataLoader;
            } else {
                throw new IllegalStateException("Error occurred while creating data loader.");
            }
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            throw new IllegalStateException("Error occurred while creating data loader.");
        }

    }


}
