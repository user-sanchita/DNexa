package com.example.ECommerce.Platform.Exception;

public class SellPriceLesserThanRealPrice extends RuntimeException {
    public SellPriceLesserThanRealPrice(String message) {
        super(message);
    }
}
