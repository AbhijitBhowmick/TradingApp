package com.trading.repository;

import com.trading.entity.InstrumentListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstrumentListRepository extends JpaRepository<InstrumentListEntity, Long> {}

