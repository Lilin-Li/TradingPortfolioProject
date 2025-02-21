package com.example.trading.model;

import com.example.trading.service.PricingService;

import java.util.Date;

public class PutOption extends Security {
    private final double strike;
    private final Date maturity;
    private double underlyingPrice;

    public PutOption(String ticker, double strike, Date maturity, double mu, double sigma) {
        super(ticker, mu, sigma);
        this.strike = strike;
        this.maturity = maturity;
    }

    @Override
    public double getPrice() {
        double T = getTimeToMaturity();
        return PricingService.calculatePutOptionPrice(underlyingPrice, strike, T, mu, sigma);
    }

    public void setUnderlyingPrice(double price) {
        this.underlyingPrice = price;
    }

    private double getTimeToMaturity() {
        long diff = maturity.getTime() - System.currentTimeMillis();
        return diff / (1000.0 * 60 * 60 * 24 * 365);
    }

    @Override
    public void setPrice(double price) {
        // Price is calculated dynamically
    }
}
