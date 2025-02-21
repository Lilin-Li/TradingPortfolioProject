package com.example.trading.db;

import java.sql.Connection;
import java.sql.SQLException;
import org.h2.jdbcx.JdbcConnectionPool;

public class ConnectionPool {
    private static final String JDBC_URL = "jdbc:h2:mem:trading;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    // 建立一個 H2 內建連線池
    private static JdbcConnectionPool cp = JdbcConnectionPool.create(JDBC_URL, USER, PASSWORD);

    public static Connection getConnection() throws SQLException {
        return cp.getConnection();
    }

    // 可選：關閉連線池的方法
    public static void shutdown() {
        cp.dispose();
    }
}
