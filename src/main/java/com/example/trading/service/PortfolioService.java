package com.example.trading.service;

import com.example.trading.db.SecurityDAO;
import com.example.trading.model.*;
import com.example.trading.model.PutOption;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PortfolioService {
    // 股票數據庫資料
    private final List<Security> securities = new ArrayList<>();
    // 持有部位
    private final List<Position> positions;


    public PortfolioService(List<Position> positions) {
        this.positions = positions;
        SecurityDAO securityDAO = new SecurityDAO(); // 新增 DAO 實例
        for (Position pos : positions) {
            // 根據 ticker 從資料庫中查詢安全資產定義
            Security sec = securityDAO.getSecurityByTicker(pos.getSymbol());
            if (sec != null) {
                securities.add(sec);
            } else {
                System.err.println("未在資料庫中找到 ticker: " + pos.getSymbol());
            }
        }
    }

    public List<Security> getSecurities() {
        return securities;
    }

    // Update the underlying price for options based on the stock price
    public void updateUnderlyingPrice(String ticker, double newPrice) {
        for (Security sec : securities) {
            // 若是股票，且 ticker 完全匹配，直接更新股票價格
            if (sec instanceof Stock) {
                if (sec.getTicker().equalsIgnoreCase(ticker)) {
                    sec.setPrice(newPrice);
                }
            }
            // 連動計算更新 相關的期權價格  (展示時動態計算)
            else if (sec instanceof CallOption) {
                CallOption callOption = (CallOption) sec;
                String underlyingTicker = extractUnderlyingTicker(callOption.getTicker());
                if (underlyingTicker.equalsIgnoreCase(ticker)) {
                    callOption.setUnderlyingPrice(newPrice);
                }
            }
            else if (sec instanceof PutOption) {
                PutOption putOption = (PutOption) sec;
                String underlyingTicker = extractUnderlyingTicker(putOption.getTicker());
                if (underlyingTicker.equalsIgnoreCase(ticker)) {
                    putOption.setUnderlyingPrice(newPrice);
                }
            }
        }
    }

    // Compute and print each position’s market value and the total portfolio NAV
    public void publishPortfolioValue() {
        double totalNAV = 0.0;
        System.out.println("## Portfolio");
        System.out.println(String.format("%-20s   %15s   %10s   %15s  ",
                "symbol",
                "price",
                "qty",
                "value"));
        for (Position pos : positions) {
            for (Security sec : securities) {
                if (sec.getTicker().equals(pos.getSymbol())) {
                    double marketValue = pos.getPositionSize() * sec.getPrice();
                    totalNAV += marketValue;
                    
                    System.out.println(String.format("%-20s   %15.2f   %10s   %15.2f  ",
                            sec.getTicker(),
                            sec.getPrice(),
                            pos.getPositionSize(),
                            marketValue));
                }
            }
        }

        System.out.println(String.format("\n%-20s   %15s   %10s   %15.2f  \n", "#Total Portfolio", "", "", totalNAV));
    }

    /**
     * 輔助方法：假設期權 ticker 格式為「股票代碼-其他資訊」，
     * 例如 "AAPL-OCT-2020-110-C"，則回傳 "-" 前的部分 "AAPL" 作為標的股票代碼。
     */
    private String extractUnderlyingTicker(String optionTicker) {
        int index = optionTicker.indexOf("-");
        if (index > 0) {
            return optionTicker.substring(0, index);
        }
        return optionTicker; // 如果找不到 "-", 直接回傳整串字
    }
}
