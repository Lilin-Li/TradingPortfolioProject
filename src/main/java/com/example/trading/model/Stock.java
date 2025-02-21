package com.example.trading.model;

public class Stock extends Security {
    private double price;

    public Stock(String ticker, double price, double mu, double sigma) {
        super(ticker, mu, sigma);
        this.price = price;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void setPrice(double price) {
        this.price = price;
    }
}
