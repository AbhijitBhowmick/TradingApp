package com.trading.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.time.Instant;


@Getter
@Setter
@Entity
@Table(name = "tick_data")
public class TickEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instrument_token", nullable = false)
    private Long instrumentToken;

    @Column(name = "last_price")
    private Double lastPrice;

    @Column(name = "last_quantity")
    private Double lastTradedQuantity;

    @Column(name = "volume")
    private Long volumeTradedToday;

    @Column(name = "buy_quantity")
    private Double totalBuyQuantity;

    @Column(name = "sell_quantity")
    private Double totalSellQuantity;

    @Column(name = "open_price")
    private Double openPrice;

    @Column(name = "high_price")
    private Double highPrice;

    @Column(name = "low_price")
    private Double lowPrice;

    @Column(name = "close_price")
    private Double closePrice;

    @Column(name = "average_price")
    private Double averageTradePrice;

    @Column(name = "change_percent")
    private Double change;

    @Column(name = "oi")
    private Double oi;

    @Column(name = "oi_day_high")
    private Double oiDayHigh;

    @Column(name = "oi_day_low")
    private Double oiDayLow;

    @Column(name = "tick_time", nullable = false)
    private Instant tickTime;

    // Avoid cascading on batch inserts (lazy load recommended)
    @OneToMany(mappedBy = "tick", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<DepthEntity> depths;


}

