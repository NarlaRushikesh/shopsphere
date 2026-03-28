package com.example.demo.messaging;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.event.OrderEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendOrderEvent(OrderEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                event
            );
            System.out.println("[OrderProducer] Event sent for order: " + event.getOrderId());
        } catch (Exception e) {
            // RabbitMQ is down — log warning, but don't crash placeOrder().
            // The order is already saved in the DB.
            System.err.println("[OrderProducer] WARNING: Could not send RabbitMQ event " +
                "for order " + event.getOrderId() + " — " + e.getMessage());
        }
    }
}