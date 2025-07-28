package com.trading.config;

import com.zerodhatech.kiteconnect.KiteConnect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {

    @Bean
    public KiteConnect kiteConnect() {
        // Initialize with your API key (not your secret)
        KiteConnect kiteConnect = new KiteConnect("neu3t85b8a4a4hoo", true);
        // Optional: enable HTTP log/debugging
        kiteConnect.setUserId("SUK704");
        return kiteConnect;
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService insertExecutor() {
        int threads = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(threads, r -> {
            Thread t = new Thread(r);
            t.setName("TickInsertThread-" + t.getId());
            t.setDaemon(true);
            return t;
        });
    }
}
