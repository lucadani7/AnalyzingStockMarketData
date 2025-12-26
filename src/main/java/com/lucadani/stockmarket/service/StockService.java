package com.lucadani.stockmarket.service;

import com.lucadani.stockmarket.entity.Stock;
import com.lucadani.stockmarket.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository repository;

    // method for reading data from database (we need it for graphics)
    public List<Stock> getStocksBySymbol(String symbol) {
        return repository.findBySymbolOrderByTradeDateAsc(symbol.toUpperCase());
    }

    @Transactional
    public void fetchAndSaveData(String symbol) {
        String cleanSymbol = symbol.trim().toUpperCase();
        List<Stock> stocksToSave = new ArrayList<>();
        Random random = new Random();
        double currentPrice = 100 + random.nextDouble() * 100;
        for (int i = 0; i < 30; ++i) {
            LocalDate tradeDate = LocalDate.now().minusDays(30 - i);
            if (!repository.existsBySymbolAndTradeDate(cleanSymbol, tradeDate)) {
                double change = (random.nextDouble() - 0.5) * 10;
                currentPrice += change;
                if (currentPrice < 0) {
                    currentPrice = 10;
                }
                Stock stock = Stock.builder()
                        .symbol(cleanSymbol)
                        .tradeDate(tradeDate)
                        .openPrice(currentPrice)
                        .closePrice(currentPrice + (random.nextDouble() * 2))
                        .highPrice(currentPrice + 5)
                        .lowPrice(currentPrice - 5)
                        .volume(1000000L + random.nextInt(500000))
                        .build();

                stocksToSave.add(stock);
            }
        }
        if (stocksToSave.isEmpty()) {
            System.out.printf("The data is already updated for %s\n", cleanSymbol);
        } else {
            repository.saveAll(stocksToSave);
            System.out.printf("Saved successfully: %d records for %s\n", stocksToSave.size(), cleanSymbol);
        }
    }
}
