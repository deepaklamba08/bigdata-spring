package com.bigdata.core.query.dialect;

public class DialectFactory {
    private static final String DIALECT_OTHER = "other";
    private static final String DIALECT_HIVE = "hive";

    public static Dialect getDialect(String dialect) {
        if (DIALECT_HIVE.equalsIgnoreCase(dialect)) {
            return new HiveDialect();
        } else if (DIALECT_OTHER.equalsIgnoreCase(dialect)) {
            return new DefaultDialect();
        } else {
            throw new IllegalStateException("unknown dialect - " + dialect);
        }
    }
}
