package com.example.demo.service;

// FILE LOCATION:
// notification-service/src/test/java/com/example/demo/service/EmailServiceTest.java

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendOrderConfirmation_sendsMailWithCorrectRecipient() {
        emailService.sendOrderConfirmation("user@example.com", 42L, 299.99);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sent = captor.getValue();
        assertNotNull(sent.getTo());
        assertEquals("user@example.com", sent.getTo()[0]);
    }

    @Test
    void sendOrderConfirmation_sendsMailWithCorrectSubject() {
        emailService.sendOrderConfirmation("user@example.com", 7L, 150.0);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        assertTrue(captor.getValue().getSubject().contains("7"));
    }

    @Test
    void sendOrderConfirmation_sendsMailWithOrderIdInBody() {
        emailService.sendOrderConfirmation("user@example.com", 55L, 0.0);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        assertTrue(captor.getValue().getText().contains("55"));
    }

    @Test
    void sendOrderConfirmation_sendsMailWithAmountInBody() {
        emailService.sendOrderConfirmation("user@example.com", 1L, 999.0);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        assertTrue(captor.getValue().getText().contains("999.0"));
    }

    @Test
    void sendOrderConfirmation_callsMailSenderExactlyOnce() {
        emailService.sendOrderConfirmation("x@example.com", 1L, 10.0);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}