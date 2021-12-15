package com.bobocode;


import com.bobocode.exception.PooledDataSourceException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PooledDataSourceTest {

    private static final String TEST_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String  USER = "postgres";
    private static final String PASS = "andersen";
    private static final Integer POOL_SIZE = 10;

    private static PooledDataSource pooledDataSource;

    @BeforeAll
    static void init() {
        pooledDataSource = new PooledDataSource(TEST_URL, USER, PASS);
        executeSQL(TestData.DROP_TABLE);
        executeSQL(TestData.CREATE_TABLE);
        executeSQL(TestData.INSERT_TEST_DATA);
    }

    static void executeSQL(String sql) {
        try (var connection = pooledDataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e ) {
            e.printStackTrace();
        }}

    @SneakyThrows
    @Test
    void oneTestForFlow() {
        //Assert all connections are free
        assertEquals(POOL_SIZE, pooledDataSource.availableConnections());
        aquireAllConnections();

        //Assert all connections are occupied
        assertEquals(0, pooledDataSource.availableConnections());
        assertThrows(PooledDataSourceException.class, () -> pooledDataSource.getConnection());

        //Assert all connection are released
        Thread.sleep(10_000);
        assertEquals(POOL_SIZE, pooledDataSource.availableConnections());
    }

    private void aquireAllConnections() throws InterruptedException {
        int count = 0;
        for (int i = 0; i < POOL_SIZE; i++) {
            Thread.sleep(100);
            new Thread(this::getConnection).start();
            count++;
        }
        System.out.println("Connections acquired: " + count);
    }

    private void getConnection() {
        try (ConnectionProxy connectionProxy = pooledDataSource.getConnection()) {
            Thread.sleep(10_000);
        }
         catch (SQLException | InterruptedException e) {
            e.printStackTrace();
         }
    }
}
