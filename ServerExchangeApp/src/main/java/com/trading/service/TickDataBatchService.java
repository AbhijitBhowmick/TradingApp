//package com.trading.service;
//
//import com.trading.entity.TickEntity;
//import com.trading.repository.TickDataRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.util.List;
//
//@Service
//public class TickDataBatchService {
//
//    @Autowired
//    private TickDataRepository tickDataRepository;
//
//    @Transactional
//    public void saveAllInBatch(List<TickEntity> ticks) {
//        tickDataRepository.saveAll(ticks);
//        // Optionally flush for immediate execution
//        tickDataRepository.flush();
//    }
//}
//
