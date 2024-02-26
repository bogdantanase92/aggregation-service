package com.tanaseb.aggregationservice.error;

public class OrderNumberException extends RuntimeException {

    public OrderNumberException(String message) {
        super(message);
    }
}
