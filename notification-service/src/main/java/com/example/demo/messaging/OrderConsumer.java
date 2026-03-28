package com.example.demo.messaging;

import com.example.demo.event.OrderEvent;
import com.example.demo.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    @Autowired
    private EmailService emailService;

    @RabbitListener(queues = "order.placed")
    public void consumeOrderEvent(OrderEvent event) {
        try {
            System.out.println("Received order event for orderId: " + event.getOrderId());

            if (event.getUserEmail() == null || event.getUserEmail().isBlank()) {
                System.err.println("Skipping email — userEmail is blank for orderId: " + event.getOrderId());
                return;
            }

            emailService.sendOrderConfirmation(
                event.getUserEmail(),
                event.getOrderId(),
                event.getTotalAmount()
            );

            System.out.println("Email sent successfully to: " + event.getUserEmail());

        } catch (MailException ex) {
            // Log and swallow — do not crash the listener so RabbitMQ does not requeue endlessly
            System.err.println("Failed to send email for orderId " + event.getOrderId()
                    + ": " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Unexpected error processing order event for orderId "
                    + event.getOrderId() + ": " + ex.getMessage());
        }
    }
}