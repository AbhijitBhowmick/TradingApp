package com.trading.mapper;

import com.trading.entity.HistoricalDataEntity;
import com.zerodhatech.models.HistoricalData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface HistoricalDataMapper {

    // Map single HistoricalData (from dataArrayList) to entity
    @Mapping(target = "id", ignore = true) // id is DB-generated
    @Mapping(target = "instrumentToken", source = "instrumentToken")
    @Mapping(target = "interval", source = "interval")
    @Mapping(target = "timestamp", source = "source.timeStamp", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "openPrice", source = "source.open")
    @Mapping(target = "highPrice", source = "source.high")
    @Mapping(target = "lowPrice", source = "source.low")
    @Mapping(target = "closePrice", source = "source.close")
    @Mapping(target = "volume", source = "source.volume")
    @Mapping(target = "openInterest", source = "source.oi")
    HistoricalDataEntity toEntity(HistoricalData source, Long instrumentToken, String interval);

    // Now, map the *outer* HistoricalData (wrapper) to List<HistoricalDataEntity> using dataArrayList
    default List<HistoricalDataEntity> toEntityListFromHistoricalData(HistoricalData historicalData, Long instrumentToken, String interval) {
        if (historicalData == null || historicalData.dataArrayList == null) {
            return List.of();
        }
        return historicalData.dataArrayList.stream()
                .map(hd -> toEntity(hd, instrumentToken, interval))
                .collect(Collectors.toList());
    }

    @Named("stringToLocalDateTime")
    default LocalDateTime mapTimeStamp(String timeStamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

        try {
             return OffsetDateTime.parse(timeStamp, formatter).toLocalDateTime();
            //return LocalDateTime.ofEpochSecond(epochMillis / 1000, 0, ZoneOffset.UTC);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid timestamp format: " + timeStamp, e);
        }
    }
}
