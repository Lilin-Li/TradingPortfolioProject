package com.example.trading.model;

public class Position {
    private final String symbol;
    private final int positionSize;

    public Position(String symbol, int positionSize) {
        this.symbol = symbol;
        this.positionSize = positionSize;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getPositionSize() {
        return positionSize;
    }
}
