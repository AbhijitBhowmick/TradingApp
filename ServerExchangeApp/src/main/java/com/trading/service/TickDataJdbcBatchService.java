//package com.trading.service;
//
//import com.trading.entity.TickEntity;
//import org.springframework.jdbc.core.BatchPreparedStatementSetter;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//import java.util.List;
//
//@Service
//public class TickDataJdbcBatchService {
//
//    private final JdbcTemplate jdbcTemplate;
//
//    public TickDataJdbcBatchService(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    public void batchInsertTickData(List<TickEntity> ticks) {
//        String sql = "INSERT INTO tick_data (instrument_token, last_price, volume, timestamp) VALUES (?, ?, ?, ?)";
//        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
//            public void setValues(java.sql.PreparedStatement ps, int i) throws java.sql.SQLException {
//                TickEntity tick = ticks.get(i);
//                ps.setLong(1, tick.getInstrumentToken());
////                ps.setDouble(2, tick.getLastTradedPrice());
////                ps.setLong(3, tick.get());
////                ps.setObject(4, tick.getTimestamp());
//            }
//            public int getBatchSize() { return ticks.size(); }
//        });
//    }
//}
//
