package com.example.demo.messaging;

// FILE LOCATION:
// notification-service/src/test/java/com/example/demo/messaging/OrderConsumerTest.java

import com.example.demo.event.OrderEvent;
import com.example.demo.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderConsumerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OrderConsumer orderConsumer;

    private OrderEvent buildEvent(Long orderId, String email, double amount) {
        OrderEvent event = new OrderEvent();
        event.setOrderId(orderId);
        event.setUserEmail(email);
        event.setTotalAmount(amount);
        event.setStatus("PLACED");
        return event;
    }

    // ─────────────────────────────────────────────
    // consumeOrderEvent()
    // ─────────────────────────────────────────────

    @Test
    void consumeOrderEvent_success_sendsEmail() {
        OrderEvent event = buildEvent(1L, "user@example.com", 499.99);

        orderConsumer.consumeOrderEvent(event);

        verify(emailService).sendOrderConfirmation("user@example.com", 1L, 499.99);
    }

    @Test
    void consumeOrderEvent_skipsEmail_whenUserEmailIsNull() {
        OrderEvent event = buildEvent(2L, null, 100.0);

        orderConsumer.consumeOrderEvent(event);

        verifyNoInteractions(emailService);
    }

    @Test
    void consumeOrderEvent_skipsEmail_whenUserEmailIsBlank() {
        OrderEvent event = buildEvent(3L, "   ", 100.0);

        orderConsumer.consumeOrderEvent(event);

        verifyNoInteractions(emailService);
    }

    @Test
    void consumeOrderEvent_doesNotThrow_whenMailExceptionOccurs() {
        OrderEvent event = buildEvent(4L, "user@example.com", 200.0);
        doThrow(new MailSendException("SMTP down"))
                .when(emailService).sendOrderConfirmation(any(), any(), anyDouble());

        // Should swallow the exception and not crash the listener
        orderConsumer.consumeOrderEvent(event);
    }

    @Test
    void consumeOrderEvent_doesNotThrow_whenUnexpectedExceptionOccurs() {
        OrderEvent event = buildEvent(5L, "user@example.com", 300.0);
        doThrow(new RuntimeException("Unexpected"))
                .when(emailService).sendOrderConfirmation(any(), any(), anyDouble());

        orderConsumer.consumeOrderEvent(event);
    }
}