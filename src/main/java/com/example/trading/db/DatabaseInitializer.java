package com.example.trading.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer {
    private static final String JDBC_URL = "jdbc:h2:mem:trading;DB_CLOSE_DELAY=-1";

    public static void initialize() {
        try (Connection conn = ConnectionPool.getConnection();
             Statement stmt = conn.createStatement()) {

            // Create table for security definitions
            String sql = "CREATE TABLE securities (" +
                    "ticker VARCHAR(50) PRIMARY KEY," +
                    "type VARCHAR(10)," +
                    "initial_price DOUBLE," +    // 開盤初始價格
                    "strike DOUBLE," +   // 行權價
                    "maturity DATE," +   // 到期日
                    "mu DOUBLE," +    // μ是股票的预期收益（假设它是股票的静态字段之一，并为每只股票分配一个介于 0 和 1 之间的唯一值）
                    "sigma DOUBLE" +  // σ是股票的年化标准差（假设它是股票的静态字段之一，并为每只股票分配一个介于 0 和 1 之间的唯一值）
                    ")";

            stmt.execute(sql);

            stmt.execute("INSERT INTO securities VALUES ('AAPL', 'STOCK', 100.0, 0, '2025-12-31', 0.3, 0.2)");
            stmt.execute("INSERT INTO securities VALUES ('AAPL-OCT-2020-110-C', 'CALL', 100.0, 110, '2028-10-20', 0.3, 0.2)");
            stmt.execute("INSERT INTO securities VALUES ('AAPL-OCT-2020-110-P', 'PUT', 100.0, 110, '2028-10-20', 0.3, 0.2)");
            stmt.execute("INSERT INTO securities VALUES ('TELSA', 'STOCK', 100.0, 0, '2025-12-31', 0.3, 0.2)");
            stmt.execute("INSERT INTO securities VALUES ('TELSA-NOV-2020-400-C', 'CALL', 100.0, 110, '2028-10-20', 0.3, 0.2)");
            stmt.execute("INSERT INTO securities VALUES ('TELSA-DEC-2020-400-P', 'PUT', 100.0, 110, '2028-10-20', 0.3, 0.2)");

            System.out.println("Database initialized with security definitions.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
