package com.example.demo.messaging;

// FILE LOCATION:
// order-service/src/test/java/com/example/demo/messaging/OrderProducerTest.java

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.event.OrderEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OrderProducer orderProducer;

    @Test
    void sendOrderEvent_sendsToCorrectExchangeAndRoutingKey() {
        OrderEvent event = new OrderEvent(1L, "user@example.com", 299.99, "PLACED");

        orderProducer.sendOrderEvent(event);

        verify(rabbitTemplate).convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
    }

    @Test
    void sendOrderEvent_doesNotThrow_whenRabbitMQIsDown() {
        OrderEvent event = new OrderEvent(2L, "user@example.com", 100.0, "PLACED");
        doThrow(new RuntimeException("Connection refused"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(OrderEvent.class));

        assertDoesNotThrow(() -> orderProducer.sendOrderEvent(event));
    }
}