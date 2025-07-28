package com.trading.processor;

import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import com.trading.config.DataSourceProvider;
import com.trading.model.TickEvent;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
@Component
public class TickDisruptorEngine {
    private final Disruptor<TickEvent> disruptor;
    private final ExecutorService processorThreadPool;
    private final AsyncTickProcessor[] asyncTickProcessors;
    private final HikariDataSource dataSource;
    private final int bufferSize = 1024;

    //private DataSourceProvider dataSourceProvider;

    public TickDisruptorEngine(HikariDataSource dataSource) {
        this.dataSource = dataSource;

        // Set thread count based on your throughput needs or CPU cores
        int numThreads = Runtime.getRuntime().availableProcessors();

        // ThreadFactory for Disruptor event handling threads
        ThreadFactory threadFactory = r -> {
            Thread t = new Thread(r);
            t.setName("DisruptorThread-" + t.getId());
            t.setDaemon(true);
            return t;
        };

        // Thread pool for async DB inserts inside handlers, if needed
        this.processorThreadPool = Executors.newFixedThreadPool(numThreads);

        // Create Disruptor with specified parallelism
        this.disruptor = new Disruptor<>(
                TickEvent.FACTORY,
                bufferSize,
                threadFactory,
                ProducerType.MULTI,
                new BusySpinWaitStrategy()
        );

        // Create one AsyncTickProcessor per consumer thread
        this.asyncTickProcessors = new AsyncTickProcessor[numThreads];
        for (int i = 0; i < numThreads; i++) {
            asyncTickProcessors[i] = new AsyncTickProcessor(dataSource, processorThreadPool);
        }
        // Register all processors for parallel event consumption
        this.disruptor.handleEventsWith(asyncTickProcessors);
    }

    public void start() {
        disruptor.start();
    }

    public void shutdown() {
        try {
            disruptor.shutdown();
        } finally {
            processorThreadPool.shutdown();
            for (AsyncTickProcessor processor : asyncTickProcessors) {
                processor.shutdown();
            }
            dataSource.close();
        }
    }

    public Disruptor<TickEvent> getDisruptor() {
        return disruptor;
    }
}
