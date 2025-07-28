//package com.trading.client;
//
//
//import com.zerodhatech.kiteconnect.KiteConnect;
//import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
//import com.zerodhatech.ticker.KiteTicker;
//import com.zerodhatech.ticker.OnConnect;
//import com.zerodhatech.ticker.OnDisconnect;
//import com.zerodhatech.ticker.OnOrderUpdate;
//import com.zerodhatech.ticker.OnTicks;
//import com.zerodhatech.models.Order;
//import com.zerodhatech.models.Tick;
//
//import java.util.ArrayList;
//
//public class ZerodhaWebSocketSample {
//    public static void main(String[] args) throws KiteException {
//        // Replace with your valid Kite API Key, Access Token, and Public Token.
//        String apiKey = "neu3t85b8a4a4hoo";
//        String accessToken = "0M8eul6ThLkYd7Hooox6OfOSf0WAsmWW";
//        //http://localhost:8080/api/redirect?action=login&type=login&status=success&request_token=0M8eul6ThLkYd7Hooox6OfOSf0WAsmWW
//        // Example: instrument token for NSE INFY (replace with tokens you need to subscribe)
//        ArrayList<Long> tokens = new ArrayList<>();
//        tokens.add(408065L); // Example token
//
//        // Initialize KiteConnect & KiteTicker
//        KiteConnect kiteSdk = new KiteConnect(apiKey);
//        kiteSdk.setAccessToken(accessToken);
//
//        KiteTicker tickerProvider = new KiteTicker(accessToken, apiKey);
//
//        // On connection established
//        tickerProvider.setOnConnectedListener(new OnConnect() {
//            @Override
//            public void onConnected() {
//                System.out.println("WebSocket connected.");
//                tickerProvsider.subscribe(tokens);
//                tickerProvider.setMode(tokens, KiteTicker.modeFull); // modeQuote or modeLTP also possible
//            }
//        });
//
//        // On disconnection
//        tickerProvider.setOnDisconnectedListener(new OnDisconnect() {
//            @Override
//            public void onDisconnected() {
//                System.out.println("WebSocket disconnected.");
//            }
//        });
//
//        // On order updates (optional)
//        tickerProvider.setOnOrderUpdateListener(new OnOrderUpdate() {
//            @Override
//            public void onOrderUpdate(Order order) {
//                System.out.println("Order Update: " + order.orderId);
//            }
//        });
//
//        // On receiving tick updates
//        tickerProvider.setOnTickerArrivalListener(new OnTicks() {
//            @Override
//            public void onTicks(ArrayList<Tick> ticks) {
//                System.out.println("Received " + ticks.size() + " ticks.");
//                for (Tick tick : ticks) {
//                    System.out.println("Last Traded Price: " + tick.getLastTradedPrice());
//                }
//            }
//        });
//
//        // Connection settings (retries, intervals)
//        tickerProvider.setTryReconnection(true);
//        tickerProvider.setMaximumRetries(10);
//        tickerProvider.setMaximumRetryInterval(30);
//
//        // Connect to WebSocket
//        tickerProvider.connect();
//
//        // Block main thread so program keeps running (you can use a better mechanism as needed)
//        try {
//            Thread.sleep(60000); // 1 minute for demo
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // Close connection if needed
//        tickerProvider.disconnect();
//    }
//}
