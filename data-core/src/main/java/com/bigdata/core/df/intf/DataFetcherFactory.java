package com.bigdata.core.df.intf;

import com.bigdata.core.query.Catalog;
import com.bigdata.core.query.QueryBuilder;
import com.bigdata.dao.intf.DataDAO;

import java.util.EnumMap;

public class DataFetcherFactory {

    public enum FetcherType {
        SingleObject, MultipleObject
    }

    private final QueryBuilder queryBuilder;
    private final DataDAO dataDAO;
    private final Catalog catalog;

    private EnumMap<FetcherType, BaseDataLoader<?>> cache;
    private final EnumMap<FetcherType, String> fetcherTypes;

    public DataFetcherFactory(QueryBuilder queryBuilder, DataDAO dataDAO, Catalog catalog, EnumMap<FetcherType, String> fetcherTypes) {
        this.queryBuilder = queryBuilder;
        this.dataDAO = dataDAO;
        this.catalog = catalog;
        this.fetcherTypes = fetcherTypes;
        this.cache = new EnumMap<>(FetcherType.class);
    }

    public <T> BaseDataLoader<T> createDataFetcher(FetcherType fetcherType) {
        BaseDataLoader<?> baseDataLoader = this.cache.get(fetcherType);
        if (baseDataLoader != null) {
            return (BaseDataLoader<T>) baseDataLoader;
        } else {
            String implClass = this.fetcherTypes.get(fetcherType);
            BaseDataLoader<T> tBaseDataLoader = this.createNew(implClass);
            this.cache.put(fetcherType, tBaseDataLoader);
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
