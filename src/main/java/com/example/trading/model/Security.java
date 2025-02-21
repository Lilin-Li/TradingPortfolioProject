package com.example.trading.model;

public abstract class Security {
    protected final String ticker;
    protected final double mu;
    protected final double sigma;

    public Security(String ticker, double mu, double sigma) {
        this.ticker = ticker;
        this.mu = mu;
        this.sigma = sigma;
    }

    public String getTicker() {
        return ticker;
    }

    public double getMu() {
        return mu;
    }

    public double getSigma() {
        return sigma;
    }

    public abstract double getPrice();
    public abstract void setPrice(double price);
}
