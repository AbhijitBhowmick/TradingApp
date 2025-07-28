package com.trading.processor;

import com.lmax.disruptor.EventHandler;
import com.trading.model.TickEvent;
import com.zerodhatech.models.Tick;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
@Component
public class AsyncTickProcessor implements EventHandler<TickEvent> {
    private static final int BATCH_SIZE = 1000;
    private final List<Tick> batchBuffer = new ArrayList<>(BATCH_SIZE);
    private final NumberFormat formatter = new DecimalFormat();

    private final DataSource dataSource;
    private final ExecutorService insertExecutor;

    public AsyncTickProcessor(DataSource dataSource, @Qualifier("insertExecutor") ExecutorService insertExecutor) {
        this.dataSource = dataSource;
        // Thread pool for DB inserts - tune pool size as needed
        this.insertExecutor = insertExecutor;


//                Executors.newFixedThreadPool(
//                Runtime.getRuntime().availableProcessors(),
//                runnable -> {
//                    Thread t = new Thread(runnable);
//                    t.setName("TickInsertThread");
//                    t.setDaemon(true);
//                    return t;
//                }
//        );
    }

    @Override
    public void onEvent(TickEvent event, long sequence, boolean endOfBatch) {
        List<Tick> ticks = event.getTicks();
        if (ticks == null || ticks.isEmpty()) return;

        batchBuffer.addAll(ticks);

        // Logging example - can be removed in production for perf
        Tick first = ticks.get(0);
        System.out.println("Received batch size: " + ticks.size());
        System.out.println("last price " + first.getLastTradedPrice());
        System.out.println("open interest " + formatter.format(first.getOi()));

        if (batchBuffer.size() >= BATCH_SIZE || endOfBatch) {
            // Create a copy for async insert and clear buffer
            List<Tick> insertBatch = new ArrayList<>(batchBuffer);
            batchBuffer.clear();

            insertExecutor.submit(() -> {
                try {
                    insertBatch(insertBatch);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void insertBatch(List<Tick> ticks) throws SQLException {
        String sql = "INSERT INTO tick_data (instrument_token, last_price, last_quantity, volume, buy_quantity, sell_quantity, open_price," +
                " high_price, low_price, close_price, average_price, change_percent, oi, oi_day_high, oi_day_low) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Tick tick : ticks) {
                ps.setLong(1, tick.getInstrumentToken());
                ps.setDouble(2, tick.getLastTradedPrice());
                ps.setDouble(3, tick.getLastTradedQuantity());
                ps.setLong(4, tick.getVolumeTradedToday());
                ps.setDouble(5, tick.getTotalBuyQuantity());
                ps.setDouble(6, tick.getTotalSellQuantity());
                ps.setDouble(7, tick.getOpenPrice());
                ps.setDouble(8, tick.getHighPrice());
                ps.setDouble(9, tick.getLowPrice());
                ps.setDouble(10, tick.getClosePrice());
                ps.setDouble(11, tick.getAverageTradePrice());
                ps.setDouble(12, tick.getChange());
                ps.setDouble(13, tick.getOi());
                ps.setDouble(14, tick.getOpenInterestDayHigh());
                ps.setDouble(15, tick.getOpenInterestDayLow());
                ps.addBatch();
            }

            ps.executeBatch();
            System.out.println("Inserted async batch of size: " + ticks.size());
        }
        catch (SQLException e) {
             e.printStackTrace();
             // Properly handle exceptions (retry, log, alert)
        }
    }

    public void shutdown() {
        insertExecutor.shutdown();
        try {
            if (!insertExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                insertExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            insertExecutor.shutdownNow();
        }
    }
}

