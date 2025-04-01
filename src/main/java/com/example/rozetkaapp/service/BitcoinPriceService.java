package com.example.rozetkaapp.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BitcoinPriceService {

    private static final String BINANCE_BTC_UAH_API_URL = "https://api.binance.com/api/v3/ticker/price?symbol=BTCUAH";

    private final RestTemplate restTemplate;

    public BitcoinPriceService() {
        this.restTemplate = new RestTemplate();
    }

    public String getBitcoinUahPrice() {
        String jsonResponse = restTemplate.getForObject(BINANCE_BTC_UAH_API_URL, String.class);

        if (jsonResponse == null || jsonResponse.isEmpty()) {
            return "Не вдалося отримати курс BTC";
        }

        return formatPrice(jsonResponse);
    }

    private String formatPrice(String jsonString) {
        JSONObject priceJson = new JSONObject(jsonString);
        String price = priceJson.getBigDecimal("price").toPlainString();
        
        return "₿ Курс Bitcoin до гривні:\n" + price + " UAH";
    }
}