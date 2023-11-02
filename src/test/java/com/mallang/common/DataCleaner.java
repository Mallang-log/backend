package com.mallang.common;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataCleaner {

    private static final String H2_FOREIGN_KEY_CHECK_FORMAT = "SET REFERENTIAL_INTEGRITY %d";
    private static final String MYSQL_FOREIGN_KEY_CHECK_FORMAT = "SET FOREIGN_KEY_CHECKS %d";
    private static final String TRUNCATE_FORMAT = "TRUNCATE TABLE %s";

    private final List<String> tableNames = new ArrayList<>();

    private DatabaseDriver databaseDriver = null;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void checkDatabase() {
        try {
            databaseDriver = DatabaseDriver.fromJdbcUrl(dataSource.getConnection().getMetaData().getURL());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void findDatabaseTableNames() {
        List<Object[]> tableInfos = entityManager.createNativeQuery("SHOW TABLES").getResultList();
        for (Object[] tableInfo : tableInfos) {
            String tableName = (String) tableInfo[0];
            tableNames.add(tableName);
        }
    }

    @Transactional
    public void clear() {
        entityManager.clear();
        truncate();
    }

    private void truncate() {
        entityManager.createNativeQuery(String.format(getDatabaseForeignKeyCheckFormat(), 0)).executeUpdate();
        for (String tableName : tableNames) {
            entityManager.createNativeQuery(String.format(TRUNCATE_FORMAT, tableName)).executeUpdate();
        }
        entityManager.createNativeQuery(String.format(getDatabaseForeignKeyCheckFormat(), 1)).executeUpdate();
    }

    private String getDatabaseForeignKeyCheckFormat() {
        return switch (databaseDriver) {
            case H2 -> H2_FOREIGN_KEY_CHECK_FORMAT;
            default -> MYSQL_FOREIGN_KEY_CHECK_FORMAT;
        };
    }
}
