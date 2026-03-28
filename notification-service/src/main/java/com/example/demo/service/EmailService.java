package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderConfirmation(String toEmail, Long orderId, double amount) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("Order Confirmed - ShopSphere #" + orderId);
        message.setText(
            "Hi,\n\n" +
            "Your order #" + orderId + " has been placed successfully!\n" +
            "Total Amount: ₹" + amount + "\n\n" +
            "Thank you for shopping with ShopSphere!\n\n" +
            "Regards,\nShopSphere Team"
        );

        mailSender.send(message);
        System.out.println("Email sent to: " + toEmail);
    }
}