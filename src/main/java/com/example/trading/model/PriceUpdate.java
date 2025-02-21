package com.example.trading.model;

public class PriceUpdate {
    private String ticker;
    private double newPrice;

    public PriceUpdate(String ticker, double newPrice) {
        this.ticker = ticker;
        this.newPrice = newPrice;
    }

    public String getTicker() {
        return ticker;
    }

    public double getNewPrice() {
        return newPrice;
    }
}
