package com.lucadani.stockmarket.repository;

import com.lucadani.stockmarket.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findBySymbolOrderByTradeDateAsc(String symbol);
    List<Stock> findBySymbolAndTradeDateBetween(String symbol, LocalDate startDate, LocalDate endDate);
    boolean existsBySymbolAndTradeDate(String symbol, LocalDate tradeDate);
}
