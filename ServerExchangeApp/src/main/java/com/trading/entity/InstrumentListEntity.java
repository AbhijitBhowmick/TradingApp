package com.trading.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "instrument_list")
public class InstrumentListEntity {

    @Id
    private Long instrumentToken;

    private String exchange;

    private String tradingsymbol;

    private String name;

    private BigDecimal lastPrice;

    private String instrumentType;

    private String segment;

    private Long exchangeToken;

    private LocalDate expiryDate;

    private BigDecimal strikePrice;

    private BigDecimal tickSize;

    private Integer lotSize;

    private LocalDateTime timestamp;
}

