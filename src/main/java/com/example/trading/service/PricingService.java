package com.example.trading.service;


public class PricingService {
    private static final double RISK_FREE_RATE = 0.02;


    /**
     * 每个期权都有固定的到期时间（假设为 𝑡 年）
     * 固定的行使价格（𝐾）
     * 你可以假设市场中的无风险利率是恒定的，记为 𝑟。对于本练习，假设它为每年 2%
     * 给定当前价格为 𝑆 的股票
     * 股票价格的年化标准差（波动率）𝜎
     * 欧洲看涨期权的价格为 𝑐
     * 欧洲看跌期权的价格为 𝑝
     */
    public static double calculateCallOptionPrice(double stockPrice, double strike, double timeToMatrity, double mu, double sigma) {
        if (timeToMatrity <= 0) {
            return Math.max(0, stockPrice - strike);
        }
        double d1 = (Math.log(stockPrice / strike) + (RISK_FREE_RATE + 0.5 * sigma * sigma) * timeToMatrity) / (sigma * Math.sqrt(timeToMatrity));
        double d2 = d1 - sigma * Math.sqrt(timeToMatrity);
        return stockPrice * cnd(d1) - strike * Math.exp(-RISK_FREE_RATE * timeToMatrity) * cnd(d2);
    }

    public static double calculatePutOptionPrice(double stockPrice, double strike, double timeToMatrity, double mu, double sigma) {
        if (timeToMatrity <= 0) {
            return Math.max(0, strike - stockPrice);
        }
        double d1 = (Math.log(stockPrice / strike) + (RISK_FREE_RATE + 0.5 * sigma * sigma) * timeToMatrity) / (sigma * Math.sqrt(timeToMatrity));
        double d2 = d1 - sigma * Math.sqrt(timeToMatrity);
        return strike * Math.exp(-RISK_FREE_RATE * timeToMatrity) * cnd(-d2) - stockPrice * cnd(-d1);
    }

    // 函数 𝑁(𝑥) 指的是根据标准正态分布分布的变量小于 𝑥 的累积概率
    private static double cnd(double x) {
        /**
         * 使用 Abramowitz 和 Stegun 的近似方法
         */
        double L = Math.abs(x);
        double k = 1.0 / (1.0 + 0.2316419 * L);
        double w = 1.0 - (1.0 / Math.sqrt(2 * Math.PI)) * Math.exp(-L * L / 2) *
                (0.31938153 * k - 0.356563782 * k * k + 1.781477937 * Math.pow(k, 3)
                        - 1.821255978 * Math.pow(k, 4) + 1.330274429 * Math.pow(k, 5));
        return x < 0 ? 1.0 - w : w;
    }
}
