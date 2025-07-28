package com.trading.service;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;
import org.springframework.stereotype.Service;

@Service
public class KiteSessionService {
    private KiteConnect kiteConnect;
    // Optionally, persist token expiry and session details

    public void initializeKiteConnect(String apiKey, String userId){
        this.kiteConnect = new KiteConnect(apiKey);
        kiteConnect.setUserId(userId);
    }

    public void createSession(String apiKey, String userId, String requestToken, String apiSecret) throws Exception, KiteException {
//        this.kiteConnect = new KiteConnect(apiKey);
////        kiteConnect.setUserId(userId);
        if (kiteConnect == null)
            throw new IllegalStateException("kiteConnect not initialized. Please intialize first.");
        User user = kiteConnect.generateSession(requestToken, apiSecret);
        kiteConnect.setAccessToken(user.accessToken);
        kiteConnect.setPublicToken(user.publicToken);
    }

    public KiteConnect getKiteConnect() {

        return kiteConnect;
    }
}

