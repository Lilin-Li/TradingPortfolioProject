package com.example.trading;

import com.example.trading.db.DatabaseInitializer;
import com.example.trading.model.Position;
import com.example.trading.model.PriceUpdate;
import com.example.trading.service.CSVPositionReader;
import com.example.trading.service.MarketDataProvider;
import com.example.trading.service.PortfolioService;
import com.example.trading.service.PortfolioSubscriber;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static void main(String[] args) {
        // 初始化資料庫及範例資料
        DatabaseInitializer.initialize();

        // 建立共享的 BlockingQueue 用於價格更新訊息傳遞，使用ArrayBlockingQueue 提高效能，減少GC
        BlockingQueue<PriceUpdate> queue = new ArrayBlockingQueue<>(100);

        // 建立市場價格提供者，從 DB 讀取股票信息後定期推送新價格到 queue
        MarketDataProvider marketDataProvider = new MarketDataProvider(queue);
        Thread marketThread = new Thread(marketDataProvider);
        marketThread.start();

        // 從 CSV 檔讀取用戶的投資組合
        List<Position> positions = CSVPositionReader.readPositions("positions.csv");
        // 利用資料庫中的安全資產定義，建立 PortfolioService（內部透過 SecurityDAO 取得股票、選擇權等資訊）
        PortfolioService portfolioService = new PortfolioService(positions);
        // 建立資產組合訂閱者，從 queue 讀取價格更新訊息並更新投資組合損益
        PortfolioSubscriber subscriber = new PortfolioSubscriber(queue, portfolioService);
        Thread subscriberThread = new Thread(subscriber);
        subscriberThread.start();
    }
}
