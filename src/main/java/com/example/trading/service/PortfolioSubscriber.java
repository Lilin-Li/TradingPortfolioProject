package com.example.trading.service;

import com.example.trading.model.PriceUpdate;
import java.util.concurrent.BlockingQueue;

public class PortfolioSubscriber implements Runnable {

    private final BlockingQueue<PriceUpdate> queue;
    private final PortfolioService portfolioService;

    public PortfolioSubscriber(BlockingQueue<PriceUpdate> queue, PortfolioService portfolioService) {
        this.queue = queue;
        this.portfolioService = portfolioService;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 等待從 Queue 中獲得價格更新
                PriceUpdate update = queue.take();
                // 根據 ticker 更新選擇權的標的價格
                portfolioService.updateUnderlyingPrice(update.getTicker(), update.getNewPrice());

                System.out.println("-----  The above formulas use double instead of BigDecimal for convenience in review, so there are precision errors -----");
                System.out.println("\n## Market Data Update: ");
                System.out.println(update.getTicker() + " -> " + update.getNewPrice() + "\n");

                // 更新並印出投資組合損益
                portfolioService.publishPortfolioValue();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
