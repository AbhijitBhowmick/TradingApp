package com.trading.model;

import com.lmax.disruptor.EventFactory;
import com.zerodhatech.models.Tick;

import java.util.List;

/**
 * Event class to hold a batch of Tick objects to be processed in the Disruptor ring buffer.
 */
public class TickEvent {
    private List<Tick> ticks;

    public List<Tick> getTicks() {
        return ticks;
    }

    public void setTicks(List<Tick> ticks) {
        this.ticks = ticks;
    }

    /**
     * Factory for pre-allocating TickEvent instances in the Disruptor ring buffer.
     */
    public static final EventFactory<TickEvent> FACTORY = TickEvent::new;
}

