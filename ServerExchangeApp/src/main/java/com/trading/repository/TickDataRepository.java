package com.trading.repository;

import com.trading.entity.TickEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TickDataRepository extends JpaRepository<TickEntity, Long> {
}