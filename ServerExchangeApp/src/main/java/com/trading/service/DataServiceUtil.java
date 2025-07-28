package com.trading.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Component
public class DataServiceUtil {

    private static final Logger logger = LoggerFactory.getLogger(DataServiceUtil.class);

    // Generic batch save method (useful for both entity types)
    @Transactional(rollbackFor = Exception.class)
    public <T> boolean saveInBatches(List<T> entities, int batchSize, JpaRepository<T, ?> repository) {
        int total = entities.size();
        for (int i = 0; i < total; i += batchSize) {
            int end = Math.min(i + batchSize, total);
            List<T> batchList = entities.subList(i, end);
            try {
                repository.saveAll(batchList);
                repository.flush();
            } catch (DataAccessException dae) {
                logger.error("Data access error during batch insert: {} - Rolling back batch from {} to {}", dae.getMessage(), i, end);
                // Optionally, throw a custom unchecked exception
                throw dae; // Propagate to trigger rollback
            } catch (Exception e) {
                logger.error("Unknown error during batch insert: {} - Rolling back batch from {} to {}", e.getMessage(), i, end);
                throw e;
            }

        }
        return true;

    }
}

