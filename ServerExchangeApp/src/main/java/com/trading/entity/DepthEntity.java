package com.trading.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tick_depth")
public class DepthEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    // REF: relationship to TickEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tick_id", nullable = false)
    private TickEntity tick;

    @Column(name = "side", length = 4, nullable = false) // "buy" or "sell"
    private String side;

    @Column(name = "level", nullable = false) // depth level, 1 to 5
    private int level;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "orders")
    private int orders;


}

