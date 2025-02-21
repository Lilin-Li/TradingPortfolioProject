package com.example.trading.service;

import com.example.trading.db.ConnectionPool;
import com.example.trading.model.PriceUpdate;
import com.example.trading.model.Security;
import com.example.trading.model.Stock;
import com.example.trading.model.CallOption;
import com.example.trading.model.PutOption;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class MarketDataProvider implements Runnable {

    private BlockingQueue<PriceUpdate> queue;
    private List<Security> securities;
    private Random random = new Random();

    public MarketDataProvider(BlockingQueue<PriceUpdate> queue) {
        this.queue = queue;
        // 讀取資料庫中所有的資產定義：股票、CALL 選擇權、PUT 選擇權
        this.securities = loadSecuritiesFromDB();
    }

    /**
     * 從資料庫讀取所有資產定義（包含股票與選擇權）
     */
    private List<Security> loadSecuritiesFromDB() {
        List<Security> securities = new ArrayList<>();
        String sql = "SELECT ticker, type, initial_price, mu, sigma, strike, maturity FROM securities";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String ticker = rs.getString("ticker");
                String type = rs.getString("type");
                double initialPrice = rs.getDouble("initial_price");
                double mu = rs.getDouble("mu");
                double sigma = rs.getDouble("sigma");

                if ("STOCK".equalsIgnoreCase(type)) {
                    // 建立股票物件
                    securities.add(new Stock(ticker, initialPrice, mu, sigma));
                } else if ("CALL".equalsIgnoreCase(type)) {
                    double strike = rs.getDouble("strike");
                    java.sql.Date maturitySql = rs.getDate("maturity");
                    java.util.Date maturity = (maturitySql != null) ? new java.util.Date(maturitySql.getTime()) : null;
                    CallOption callOption = new CallOption(ticker, strike, maturity, mu, sigma);
                    // 將初始價格作為標的物價格
                    callOption.setUnderlyingPrice(initialPrice);
                    securities.add(callOption);
                } else if ("PUT".equalsIgnoreCase(type)) {
                    double strike = rs.getDouble("strike");
                    java.sql.Date maturitySql = rs.getDate("maturity");
                    java.util.Date maturity = (maturitySql != null) ? new java.util.Date(maturitySql.getTime()) : null;
                    PutOption putOption = new PutOption(ticker, strike, maturity, mu, sigma);
                    putOption.setUnderlyingPrice(initialPrice);
                    securities.add(putOption);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return securities;
    }

    @Override
    public void run() {
        // 僅對股票進行價格模擬更新，其它投資物件（選擇權）不直接模擬價格，
        // 選擇權價格會透過 PortfolioSubscriber 根據底層股票的更新來計算
        while (true) {
            for (Security sec : securities) {
                if (sec instanceof Stock) {
                    Stock stock = (Stock) sec;

                    /**
                     * 簡化起見 不使用BigDecimal ，而使用 double 方便理解公式
                     * 實際需將所有函數替換為 BigDecimal
                     */

                    // s
                    double currentPrice = stock.getPrice();
                    // 在 0.5 到 2 秒之间随机的离散时间几何布朗运动 （此處 dt 為模擬時間步長）
                    double dt = 0.5 + (1.5 * random.nextDouble());
                    // μ是股票的预期收益（假设它是股票的静态字段之一，并为每只股票分配一个介于 0 和 1 之间的唯一值）
                    double mu = stock.getMu();
                    // σ是股票的年化标准差（假设它是股票的静态字段之一，并为每只股票分配一个介于 0 和 1 之间的唯一值）
                    double sigma = stock.getSigma();
                    // ε是一个随机变量，每次调用此公式时，它是从标准正态分布中抽取的。
                    double epsilon = random.nextGaussian();
                    // newPrice = s + Δ𝑆
                    double newPrice = currentPrice + currentPrice * (mu * ( dt / 7257600 ) + sigma * epsilon * Math.sqrt(dt / 7257600));

                    // 股票的价格永远不能低于 0。您可以为系统中支持的每只股票选择一个您想要的开盘价来开始交易日。
                    newPrice = Math.max(newPrice, 0);

                    // 封裝價格更新訊息並推送到 Queue 中
                    PriceUpdate update = new PriceUpdate(stock.getTicker(), newPrice);
                    try {
                        queue.put(update);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
