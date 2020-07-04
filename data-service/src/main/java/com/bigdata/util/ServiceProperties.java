package com.bigdata.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "bigdata.svc")
public class ServiceProperties {
    @Value("${bigdata.svc.graphql.schema.file}")
    private String graphqlSchemaFilePath;

    @Value("${bigdata.svc.db.mapping.file}")
    private String databaseMappingFilePath;

    @Value("${bigdata.svc.data.loader.file}")
    private String dataLoaderConfigFilePath;

    private Map<String, String> dbCred;

    public String getDataLoaderConfigFilePath() {
        return dataLoaderConfigFilePath;
    }

    public String getGraphqlSchemaFilePath() {
        return graphqlSchemaFilePath;
    }

    public String getDatabaseMappingFilePath() {
        return databaseMappingFilePath;
    }

    public Map<String, String> getDbCred() {
        return dbCred;
    }

    public void setDbCred(Map<String, String> dbCred) {
        this.dbCred = dbCred;
    }
}
