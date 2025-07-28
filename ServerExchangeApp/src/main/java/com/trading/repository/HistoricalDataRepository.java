package com.trading.repository;

import com.trading.entity.HistoricalDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface HistoricalDataRepository extends JpaRepository<HistoricalDataEntity, Long> {}
