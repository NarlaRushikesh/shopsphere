package com.example.demo.exception;

import java.util.List;

public class InvalidOrderStatusException extends RuntimeException {

    private static final List<String> VALID_STATUSES =
            List.of("PLACED", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED");

    public InvalidOrderStatusException(String status) {
        super("Invalid order status: '" + status + "'. Allowed values: " + VALID_STATUSES);
    }
}