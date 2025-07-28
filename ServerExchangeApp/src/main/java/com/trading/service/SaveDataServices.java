//package com.trading.service;
//
//import com.trading.entity.HistoricalDataEntity;
//import com.trading.entity.InstrumentListEntity;
//import com.trading.repository.HistoricalDataRepository;
//import com.trading.repository.InstrumentListRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//
//@Component
//public class SaveDataServices {
//    @Autowired
//    private HistoricalDataRepository historicalDataRepository;
//
//    @Autowired
//    private InstrumentListRepository instrumentListRepository;
//
//    @Autowired
//    private DataServiceUtil dataServiceUtil;
//
//
//    public boolean saveHistoricalDataInBatches(List<HistoricalDataEntity> dataList) {
//
//        try {
//            dataServiceUtil.saveInBatches(dataList, 1000, historicalDataRepository);
//            return true;
//        } catch (Exception ex) {
//            // You can do additional actions here if needed
//            // e.g. notify ops team, metrics, etc.
//            return false;
//        }
//    }
//
//    public void saveInstrumentDataInBatches(List<InstrumentListEntity> dataList) {
//        try {
//            dataServiceUtil.saveInBatches(dataList, 1000, instrumentListRepository);
//        } catch (Exception ex) {
//            // You can do additional actions here if needed
//            // e.g. notify ops team, metrics, etc.
//        }
//
//    }
//}
