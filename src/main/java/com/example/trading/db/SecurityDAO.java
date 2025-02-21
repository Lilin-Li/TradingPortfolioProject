package com.example.trading.db;

import com.example.trading.model.Security;
import com.example.trading.model.Stock;
import com.example.trading.model.CallOption;
import com.example.trading.model.PutOption;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class SecurityDAO {
    // 與 DatabaseInitializer 保持相同的連接 URL
    private static final String JDBC_URL = "jdbc:h2:mem:trading;DB_CLOSE_DELAY=-1";

    /**
     * 根據 ticker 從資料庫查詢安全資產定義，並返回對應的 Security 物件
     *
     * @param ticker 股票或選擇權的代碼
     * @return 對應的 Security 物件，如果查詢不到則返回 null
     */
    public Security getSecurityByTicker(String ticker) {
        String sql = "SELECT ticker, initial_price, type, strike, maturity, mu, sigma FROM securities WHERE ticker = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ticker);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String type = rs.getString("type");
                    double mu = rs.getDouble("mu");
                    double sigma = rs.getDouble("sigma");
                    double initialPrice = rs.getDouble("initial_price");

                    // 如果是股票，給定一個預設初始價格（例如 100.0）
                    if ("STOCK".equalsIgnoreCase(type)) {
                        return new Stock(ticker, initialPrice, mu, sigma);
                    }
                    // 如果是 CALL 選擇權
                    else if ("CALL".equalsIgnoreCase(type)) {
                        double strike = rs.getDouble("strike");
                        Date maturity = new Date(rs.getDate("maturity").getTime());
                        CallOption callOption = new CallOption(ticker, strike, maturity, mu, sigma);
                        callOption.setUnderlyingPrice(initialPrice); // 使用資料庫中的初始價格
                        return callOption;
                    } else if ("PUT".equalsIgnoreCase(type)) {
                        double strike = rs.getDouble("strike");
                        Date maturity = new Date(rs.getDate("maturity").getTime());
                        PutOption putOption = new PutOption(ticker, strike, maturity, mu, sigma);
                        putOption.setUnderlyingPrice(initialPrice);
                        return putOption;
                    } else {
                        System.err.println("未知的資產類型: " + type);
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
