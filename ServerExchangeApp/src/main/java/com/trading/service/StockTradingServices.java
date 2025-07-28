package com.trading.service;

import com.trading.entity.HistoricalDataEntity;
import com.trading.entity.InstrumentListEntity;
import com.trading.mapper.HistoricalDataMapper;
import com.trading.mapper.InstrumentMapper;
import com.trading.repository.HistoricalDataRepository;
import com.trading.repository.InstrumentListRepository;
import com.trading.utility.DateUtils;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Instrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static java.lang.Long.getLong;

@Component
public class StockTradingServices {

//    @Autowired
//    private SaveDataServices  saveDataServices;

    @Autowired
    private HistoricalDataMapper historicalDataMapper;

    @Autowired
    private InstrumentMapper instrumentMapper;

    @Autowired
    private HistoricalDataRepository historicalDataRepository;

    @Autowired
    private InstrumentListRepository instrumentListRepository;

    @Autowired
    private DataServiceUtil dataServiceUtil;


    public ResponseEntity<Boolean> getHistoricalDataResponseEntity(KiteSessionService sessionService, Date fromDate, Date toDate, String instrumentToken, String interval, boolean continuous, boolean oi) throws KiteException, IOException, ParseException {
        /** Get historical data dump, requires from and to date, intrument token, interval, continuous (for expired F&O contracts), oi (open interest)
         * returns historical data object which will have list of historical data inside the object.*/
        List<DateUtils.DateRange> dateRanges = DateUtils.splitIntoHalfYearlyRanges(fromDate, toDate);
        for (DateUtils.DateRange dateRange :dateRanges) {

            List<HistoricalDataEntity> entities =
                    historicalDataMapper.toEntityListFromHistoricalData(sessionService.getKiteConnect().getHistoricalData(dateRange.getFrom(), dateRange.getTo(), instrumentToken, interval, continuous, oi), Long.parseLong(instrumentToken), interval);

            // Use earlier batch save method, e.g.
            boolean isBatchDataSaved = dataServiceUtil.saveInBatches(entities, 1000, historicalDataRepository);
        }
        //HistoricalData historicalData = kiteConnect.getHistoricalData(from, to, "54872327", "15minute", false, true);
        return ResponseEntity.ok(true);
    }

    public ResponseEntity<Boolean> getAllInstruments(List<Instrument> instruments) throws KiteException, IOException, ParseException {
        /** Get historical data dump, requires from and to date, intrument token, interval, continuous (for expired F&O contracts), oi (open interest)
         * returns historical data object which will have list of historical data inside the object.*/

        // Map Instrument to InstrumentListEntity
        List<InstrumentListEntity> instrumentListEntityList = instrumentMapper.instrumentListToInstrumentListEntityList( instruments);

            // Use earlier batch save method, e.g.
            boolean isBatchDataSaved = dataServiceUtil.saveInBatches(instrumentListEntityList, 1000, instrumentListRepository);
        //HistoricalData historicalData = kiteConnect.getHistoricalData(from, to, "54872327", "15minute", false, true);
        return ResponseEntity.ok(true);
    }
}
