package com.bobocode;

import com.bobocode.exception.PooledDataSourceException;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PooledDataSource extends PGSimpleDataSource {

    public static final Integer POOL_SIZE = 10;

    private static final ConcurrentLinkedQueue<ConnectionProxy> availableConnectionsQueue = new ConcurrentLinkedQueue<>();

    public PooledDataSource(String url, String user, String password) {
        super();
        setURL(url);
        setUser(user);
        setPassword(password);
        initPool();
    }

    private void initPool() {
        for(int i = 0; i < POOL_SIZE; i++) {
            try {
                availableConnectionsQueue.add(new ConnectionProxy(super.getConnection(), this));
            } catch (SQLException e) {
                throw new PooledDataSourceException("Couldn't acquire connection during pool initialization");
            }
        }

    }

    @Override
    public ConnectionProxy getConnection() {
        if (availableConnectionsQueue.isEmpty()) {
            throw new PooledDataSourceException("No available free connections");
        }

        return availableConnectionsQueue.poll();
    }

    public void returnConnection(ConnectionProxy connectionProxy) {
        availableConnectionsQueue.add(connectionProxy);
    }


    public int availableConnections() {
        return availableConnectionsQueue.size();
    }
}
