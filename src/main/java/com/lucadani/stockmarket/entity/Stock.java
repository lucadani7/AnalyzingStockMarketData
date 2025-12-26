package com.lucadani.stockmarket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The stock symbol is mandatory")
    @Column(nullable = false, length = 10)
    private String symbol;

    @NotNull(message = "The transaction data is mandatory")
    private LocalDate tradeDate;

    @Positive(message = "The price must be positive")
    private Double openPrice;

    @Positive(message = "The price must be positive")
    private Double closePrice;

    @Positive(message = "The price must be positive")
    private Double highPrice;

    @Positive(message = "The price must be positive")
    private Double lowPrice;

    @PositiveOrZero
    private Long volume;
}
