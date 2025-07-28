package com.trading.controller;

import com.neovisionaries.ws.client.WebSocketException;
import com.trading.model.TickEvent;
import com.trading.processor.TickDisruptorEngine;
import com.trading.service.KiteSessionService;
import com.trading.service.StockTradingServices;
import com.trading.utility.TradeUtility;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.*;
import com.zerodhatech.ticker.*;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@RestController
@RequestMapping("/api/")
public class StockTradingApplicationController {
    public static final String apiKey = "neu3t85b8a4a4hoo";
    public static final String userId = "SUK704";
    public static final String API_SECRET = "ylj7f80bdxo3020y3ak2oyde4pyuv2vs";

    @Autowired
    private TickDisruptorEngine tickDisruptorEngine;

    @Autowired
    private StockTradingServices stockTradingServices;

    private static final TimeZone IST = TimeZone.getTimeZone("Asia/Kolkata");


//    @Value("${custom.message}")
//    private String message;



    private final KiteSessionService sessionService;

    @Autowired
    public StockTradingApplicationController( KiteSessionService sessionService) {
        this.sessionService = sessionService;
    }




    @GetMapping("/getSession")
    public void getSession()  {
        try {
            sessionService.initializeKiteConnect(apiKey, userId);
             // Generate login URL
            String loginUrl = sessionService.getKiteConnect().getLoginURL();
            System.out.println("Login URL: " + loginUrl);
            // Open the login URL in the default browser (macOS)
            openBrowser(loginUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void openBrowser(String url) throws IOException {
        // Open URL in default browser on macOS
        new ProcessBuilder("open", url).start(); // macOS command to open a URL in the default browser
    }

    @GetMapping("/redirect")
    public ResponseEntity<String> redirect(HttpServletRequest httpServletRequest) throws Exception {

        try {
        // Parse query parameters
        Map<String, String> queryMap = parseQuery(httpServletRequest.getQueryString());

        // Extract the request token
        String requestToken = queryMap.get("request_token");
        TradeUtility.writeToken(requestToken);

        return getDataWithRequestToken(requestToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/getDataWithRequestToken")
    public ResponseEntity<String> getDataWithRequestToken(@RequestParam String requestToken) throws Exception {
        try {
            if(null== requestToken && TradeUtility.readToken().isPresent()){
                requestToken=TradeUtility.readToken().get();
            }
            System.out.println("Request Token: " + requestToken);
            sessionService.createSession(apiKey, userId, requestToken, API_SECRET);
            return ResponseEntity.ok("Session created");
        }catch(Exception | KiteException e){
            throw new Exception();
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() throws IOException, KiteException {
        return ResponseEntity.ok(sessionService.getKiteConnect().getProfile());
    }

    @GetMapping("/allInstruments")
    public ResponseEntity<?> getAllInstruments() throws IOException, KiteException, ParseException {
        List<Instrument> instruments = sessionService.getKiteConnect().getInstruments();
        TradeUtility.convertJsonToCsv(instruments, "instruments.csv");
        stockTradingServices.getAllInstruments(instruments);
        return ResponseEntity.ok("All Instrument Returned");
    }
    @GetMapping("/margins")
    public ResponseEntity<?> getMargins() throws IOException, KiteException {
        return ResponseEntity.ok(sessionService.getKiteConnect().getMargins("equity"));
    }

    /** Get holdings.*/
    @GetMapping("/getHoldings")
    public ResponseEntity<?> getHoldings() throws KiteException, IOException {
        // Get holdings returns holdings model which contains list of holdings.
        return ResponseEntity.ok(sessionService.getKiteConnect().getHoldings());
    }
    /** GetInstrumentsForExchange.*/
    @GetMapping("/getInstrumentsForExchange")
    public ResponseEntity<?> getInstrumentsForExchange(@RequestParam String exchange) throws KiteException, IOException {
        // Get instruments for an exchange.
        return ResponseEntity.ok(sessionService.getKiteConnect().getInstruments(exchange));
    }

    /**  getQuote.*/
    @GetMapping("/getQuote")
    public ResponseEntity<?> getQuote() throws KiteException, IOException {
        // Get quotes returns quote for desired tradingsymbol.
        String[] instruments = {"256265","BSE:INFY", "NSE:APOLLOTYRE", "NSE:NIFTY 50", "24507906"};
        //Map<String, Quote> quotes = kiteConnect.getQuote(instruments);
        return ResponseEntity.ok(sessionService.getKiteConnect().getQuote(instruments));
    }

    /* Get ohlc and lastprice for multiple instruments at once.
     * Users can either pass exchange with tradingsymbol or instrument token only. For example {NSE:NIFTY 50, BSE:SENSEX} or {256265, 265}*/
    @GetMapping("/getOHLC")
    public ResponseEntity<?> getOHLC() throws KiteException, IOException {
        String[] instruments = {"256265","BSE:INFY", "NSE:INFY", "NSE:NIFTY 50"};
        //System.out.println(kiteConnect.getOHLC(instruments).get("256265").lastPrice);
        return ResponseEntity.ok(sessionService.getKiteConnect().getOHLC(instruments).get("256265"));
    }
    /** Get last price for multiple instruments at once.
     * USers can either pass exchange with tradingsymbol or instrument token only. For example {NSE:NIFTY 50, BSE:SENSEX} or {256265, 265}*/
    @GetMapping("/getLTP")
    public ResponseEntity<?> getLTP() throws KiteException, IOException {
        // Get holdings returns holdings model which contains list of holdings.
        String[] instruments = {"256265","BSE:INFY", "NSE:INFY", "NSE:NIFTY 50"};
        return ResponseEntity.ok(sessionService.getKiteConnect().getLTP(instruments).get("256265").lastPrice);
    }
    /** Get historical data for an instrument.*/
    @GetMapping("/getHistoricalData")
    public ResponseEntity<?> getHistoricalData(@RequestParam String fromDate, @RequestParam String toDate, @RequestParam String instrumentToken,
                                               @RequestParam String interval, @RequestParam boolean continuous , @RequestParam boolean oi) throws KiteException, IOException, ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(IST);
        Date from = null;
        Date to = null;
        try {
            from = formatter.parse(fromDate);
            to = formatter.parse(toDate);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return stockTradingServices.getHistoricalDataResponseEntity(sessionService,from, to, instrumentToken, interval, continuous, oi);
    }



    /** Get historical data for an instrument.*/
    @GetMapping("/getHistoricalDataYearWise")
    public ResponseEntity<?> getHistoricalDataYearWise(@RequestParam String fromYear, @RequestParam String toYear, @RequestParam String instrumentToken,
                                               @RequestParam String interval, @RequestParam boolean continuous , @RequestParam boolean oi) throws KiteException, IOException, ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(IST);
        Date from = null;
        Date to = null;
        try {
            from = formatter.parse(fromYear+"-01-01 09:15:00");
            to = formatter.parse(toYear+"-12-31 15:30:00");
        }catch (ParseException e) {
            e.printStackTrace();
        }


        //HistoricalData historicalData = kiteConnect.getHistoricalData(from, to, "54872327", "15minute", false, true);

        return stockTradingServices.getHistoricalDataResponseEntity(sessionService, from, to, instrumentToken, interval, continuous, oi);
    }


    /** Demonstrates com.zerodhatech.ticker connection, subcribing for instruments, unsubscribing for instruments, set mode of tick data, com.zerodhatech.ticker disconnection*/
    @GetMapping("/tickerUsage")
    public void tickerUsage(ArrayList<Long> tokens) throws IOException, WebSocketException, KiteException {
        if(tokens.isEmpty()){
            // Example: Create list of Zerodha tokens (Long format)
             tokens.addAll(Arrays.asList(
                    738561L,
                     408065L,
                     5633L,
                     264969L
            ));
        }
        /** To get live price use websocket connection.
         * It is recommended to use only one websocket connection at any point of time and make sure you stop connection, once user goes out of app.
         * custom url points to new endpoint which can be used till complete Kite Connect 3 migration is done. */
        final KiteTicker tickerProvider = new KiteTicker(sessionService.getKiteConnect().getAccessToken(), sessionService.getKiteConnect().getApiKey());

        tickerProvider.setOnConnectedListener(new OnConnect() {
            @Override
            public void onConnected() {
                /** Subscribe ticks for token.
                 * By default, all tokens are subscribed for modeQuote.
                 * */
                tickerProvider.subscribe(tokens);
                tickerProvider.setMode(tokens, KiteTicker.modeFull);
            }
        });

        tickerProvider.setOnDisconnectedListener(new OnDisconnect() {
            @Override
            public void onDisconnected() {
                // your code goes here
            }
        });

        /** Set listener to get order updates.*/
        tickerProvider.setOnOrderUpdateListener(new OnOrderUpdate() {
            @Override
            public void onOrderUpdate(Order order) {
                System.out.println("order update "+order.orderId);
            }
        });

        /** Set error listener to listen to errors.*/
        tickerProvider.setOnErrorListener(new OnError() {
            @Override
            public void onError(Exception exception) {
                //handle here.
            }

            @Override
            public void onError(KiteException kiteException) {
                //handle here.
            }

            @Override
            public void onError(String error) {
                System.out.println(error);
            }
        });
        tickerProvider.setOnTickerArrivalListener(new OnTicks() {
            @Override
            public void onTicks(ArrayList<Tick> ticks) {
                var ringBuffer = tickDisruptorEngine.getDisruptor().getRingBuffer();
                long sequence = ringBuffer.next();
                try {
                    TickEvent event = ringBuffer.get(sequence);
                    event.setTicks(ticks);
                    NumberFormat formatter = new DecimalFormat();
                System.out.println("ticks size "+ticks.size());
                if(ticks.size() > 0) {
                    System.out.println("last price " + ticks.get(0).getLastTradedPrice());
                    System.out.println("open interest " + formatter.format(ticks.get(0).getOi()));
                    System.out.println("day high OI " + formatter.format(ticks.get(0).getOpenInterestDayHigh()));
                    System.out.println("day low OI " + formatter.format(ticks.get(0).getOpenInterestDayLow()));
                    System.out.println("change " + formatter.format(ticks.get(0).getChange()));
                    System.out.println("tick timestamp " + ticks.get(0).getTickTimestamp());
                    System.out.println("tick timestamp date " + ticks.get(0).getTickTimestamp());
                    System.out.println("last traded time " + ticks.get(0).getLastTradedTime());
                    System.out.println(ticks.get(0).getMarketDepth().get("buy").size());
                }
                } finally {
                    ringBuffer.publish(sequence);
                }

            }
        });

//        tickerProvider.setOnTickerArrivalListener(new OnTicks() {
//            @Override
//            public void onTicks(ArrayList<Tick> ticks) {
//                NumberFormat formatter = new DecimalFormat();
//                System.out.println("ticks size "+ticks.size());
//                if(ticks.size() > 0) {
//                    System.out.println("last price "+ticks.get(0).getLastTradedPrice());
//                    System.out.println("open interest "+formatter.format(ticks.get(0).getOi()));
//                    System.out.println("day high OI "+formatter.format(ticks.get(0).getOpenInterestDayHigh()));
//                    System.out.println("day low OI "+formatter.format(ticks.get(0).getOpenInterestDayLow()));
//                    System.out.println("change "+formatter.format(ticks.get(0).getChange()));
//                    System.out.println("tick timestamp "+ticks.get(0).getTickTimestamp());
//                    System.out.println("tick timestamp date "+ticks.get(0).getTickTimestamp());
//                    System.out.println("last traded time "+ticks.get(0).getLastTradedTime());
//                    System.out.println(ticks.get(0).getMarketDepth().get("buy").size());
//                }
//            }
//        });
        // Make sure this is called before calling connect.
        tickerProvider.setTryReconnection(true);
        //maximum retries and should be greater than 0
        tickerProvider.setMaximumRetries(10);
        //set maximum retry interval in seconds
        tickerProvider.setMaximumRetryInterval(30);

        /** connects to com.zerodhatech.com.zerodhatech.ticker server for getting live quotes*/
        tickerProvider.connect();

        /** You can check, if websocket connection is open or not using the following method.*/
        boolean isConnected = tickerProvider.isConnectionOpen();
        System.out.println(isConnected);

        /** set mode is used to set mode in which you need tick for list of tokens.
         * Ticker allows three modes, modeFull, modeQuote, modeLTP.
         * For getting only last traded price, use modeLTP
         * For getting last traded price, last traded quantity, average price, volume traded today, total sell quantity and total buy quantity, open, high, low, close, change, use modeQuote
         * For getting all data with depth, use modeFull*/
        tickerProvider.setMode(tokens, KiteTicker.modeFull);

        // Unsubscribe for a token.
        //tickerProvider.unsubscribe(tokens);

        // After using com.zerodhatech.com.zerodhatech.ticker, close websocket connection.
        //tickerProvider.disconnect();
    }




    public static Map<String, String> parseQuery(String query) throws Exception {
        Map<String, String> queryMap = new HashMap<>();
        String[] params = query.split("&");

        for (String param : params) {
            String[] parts = param.split("=");
            if (parts.length == 2) {
                String name = URLDecoder.decode(parts[0], "UTF-8");
                String value = URLDecoder.decode(parts[1], "UTF-8");
                queryMap.put(name, value);
            }
        }

        return queryMap;
    }

}
