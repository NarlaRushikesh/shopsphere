package com.example.demo.exception;

public class EmptyCartException extends RuntimeException {
    public EmptyCartException(String email) {
        super("Cart is empty for user: " + email);
    }
}