package com.trading.mapper;

import com.trading.entity.InstrumentListEntity;
import com.zerodhatech.models.Instrument;
import org.mapstruct.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring")
public interface InstrumentMapper {

    InstrumentMapper INSTANCE = Mappers.getMapper(InstrumentMapper.class);

    @Mapping(target = "expiryDate", source = "expiry", qualifiedByName = "dateToLocalDate")
    @Mapping(target = "strikePrice", source = "strike", qualifiedByName = "stringToBigDecimal")
    @Mapping(target = "lastPrice", source = "last_price")
    @Mapping(target = "instrumentToken", source = "instrument_token")
    @Mapping(target = "exchangeToken", source = "exchange_token")
    @Mapping(target = "tickSize", source = "tick_size")
    @Mapping(target = "lotSize", source = "lot_size")
    @Mapping(target = "timestamp", ignore = true)
    InstrumentListEntity instrumentToInstrumentListEntity(Instrument instrument);

    List<InstrumentListEntity> instrumentListToInstrumentListEntityList(List<Instrument> instrumentList);

    @Named("dateToLocalDate")
    public static LocalDate mapDateToLocalDate(Date date) {
        return (date == null) ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Named("stringToBigDecimal")
    public static BigDecimal mapStringToBigDecimal(String strike) {
        try {
            return (strike != null && !strike.trim().isEmpty()) ? new BigDecimal(strike) : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
