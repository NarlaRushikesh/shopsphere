package com.example.demo.exception;

import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Email sending failed (wrong SMTP config, network issue etc.)
    @ExceptionHandler(MailException.class)
    public ResponseEntity<?> handleMailException(MailException ex) {
        return build(HttpStatus.SERVICE_UNAVAILABLE,
                "Failed to send email notification: " + ex.getMessage());
    }

    // RabbitMQ message processing failed
    @ExceptionHandler(ListenerExecutionFailedException.class)
    public ResponseEntity<?> handleRabbitListener(ListenerExecutionFailedException ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to process order event from queue: " + ex.getMessage());
    }

    // 500 — anything else
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Notification service error: " + ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", message);
        return ResponseEntity.status(status).body(body);
    }
}